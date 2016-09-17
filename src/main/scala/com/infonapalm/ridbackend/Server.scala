package com.infonapalm.ridbackend

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.infonapalm.ridbackend.actors._
import com.infonapalm.ridbackend.models.{Friends, Photos, Tags, Videos}
import com.infonapalm.ridbackend.parsers.{FriendsParser, PhotosParser, VideosParser}
import com.mchange.v2.c3p0.ComboPooledDataSource
import com.mchange.v2.c3p0.ComboPooledDataSource
import com.rabbitmq.client.{AMQP, Channel, ConnectionFactory, DefaultConsumer}
import com.thenewmotion.akka.rabbitmq._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.CommonFilters
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.logging.filter.{LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.logging.modules.Slf4jBridgeModule
import com.twitter.inject.Logging

import scala.concurrent.duration._
import scala.slick.jdbc.JdbcBackend._
import scala.util.Random

object ServerMain extends Server with Logging {
  val isCronServer = Option(System.getenv("IS_CRON")).getOrElse("false").toBoolean

  val cpds = new ComboPooledDataSource
  val db = Database.forDataSource(cpds)

  val ActorSystemName = if(isCronServer){
    "BackendAkkaSystemCron"+System.currentTimeMillis()
  }else{
    "BackendAkkaSystem"+System.currentTimeMillis()
  }

  info(s"Start server with actor system ${ActorSystemName}")

  lazy val system = ActorSystem(ActorSystemName)
  lazy val dbActor = system.actorOf(RoundRobinPool(3).props(Props[DBActor]), name = "dbActor")
  lazy val photosActor = system.actorOf(RoundRobinPool(7).props(Props[PhotosParserActor]), name = "photosActor")
  lazy val googleVisualActor = system.actorOf(RoundRobinPool(5).props(Props[GoogleVisualActor]), name = "googleVisualActor")

  lazy val factory = new ConnectionFactory()
  lazy val connection = system.actorOf(ConnectionActor.props(factory), "rabbitmq")
  val exchangeUsers = "users"
  val exchangeUsers2ndRound = "users2ndRound"
  val exchangeRussians = "russians"
  val exchangeRussiansToUpdate = "russiansToUpdate"
  val exchangeUsersWithTags = "usersWithTags"

  implicit val timeout = Timeout(60 seconds)
  implicit val database = db

  def setupPublisherUsers(channel: Channel, self: ActorRef) {
    val queue = channel.queueDeclare("users",true,false,false,null).getQueue
    channel.queueBind(queue, exchangeUsers, "")
    val consumer = new DefaultConsumer(channel) with Logging {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]) {
        val (fromUser,user) = (fromBytes(body).split(",")(0),fromBytes(body).split(",")(1))
        info("received User: " + fromBytes(body))
        val userInfo = new FriendsParser().getUserInfo(user)
        Friends.saveToDB(userInfo.head)
        val friends = userInfo.map(_.copy(request_from = fromUser))
        info("SAVING. Size to save friends: " + friends.length)
        friends.foreach(Friends.saveToDB(_))
        channel.basicAck(envelope.getDeliveryTag, false)
      }
    }
    channel.basicQos(1)
    channel.basicConsume(queue, false, consumer)
  }
  def setupPublisherRussians(channel: Channel, self: ActorRef) {
    val queue = channel.queueDeclare("russians",true,false,false,null).getQueue
    channel.queueBind(queue, exchangeRussians, "")
    val consumer = new DefaultConsumer(channel) with Logging {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]) {
        info("received Russian: " + fromBytes(body))
        val userInfo = if(fromBytes(body).contains("~")){
          //We have some data to parse before we get a uid. In current moment this is for uid+tag+comment
          val (link,tag,comment) = (fromBytes(body).split("~")(0),fromBytes(body).split("~")(1),fromBytes(body).split("~")(2))
          val userInfoByURI = new FriendsParser().getUserByURI(link)
          userInfoByURI match {
            case Some(parsedUserInfo) => {
              val ui = parsedUserInfo.copy(group_id = Friends.RUSSIAN_GROUP_IDX,comments = comment)
              dbActor ! new SaveFriendMsg(List(ui))
              dbActor ! new SavePhotoMsg(new PhotosParser().getAllPhotos(ui.uid))
              Tags.appendTag(ui.uid,tag)
              Some(ui)
            }
            case None => {
              None
            }
          }
        }else{
          //Only uid present in message
          val ui = new FriendsParser().getUserInfo(fromBytes(body)).map(u => u.copy(group_id = Friends.RUSSIAN_GROUP_IDX))
          dbActor ! new SaveFriendMsg(ui)
          dbActor ! new SavePhotoMsg(new PhotosParser().getAllPhotos(ui.head.uid))
          Some(ui.head)
        }

        userInfo match {
          case Some(info) => {
            def publish(channel: Channel) {
              new FriendsParser().getAllFriends(info.uid).foreach(x => {
                channel.basicPublish(exchangeUsers, "", (new AMQP.BasicProperties.Builder()).deliveryMode(2).build(), toBytes((info.uid+","+x.uid)))
              })
            }
            val publisher = system.actorFor("/user/rabbitmq/publisherUsers")
            publisher ! ChannelMessage(publish, dropIfNoChannel = false)
            Friends.setGroup(info.uid,Friends.RUSSIAN_GROUP_IDX)
          }
        }
      }
    }
    channel.basicQos(10)
    channel.basicConsume(queue, true, consumer)
  }
  def setupPublisherRussiansToUpdate(channel: Channel, self: ActorRef) {
    val queue = channel.queueDeclare(exchangeRussiansToUpdate,true,false,false,null).getQueue
    channel.queueBind(queue, exchangeRussiansToUpdate, "")
    val consumer = new DefaultConsumer(channel) with Logging {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]) {
        val uid = fromBytes(body)
        info("setupPublisherRussiansToUpdate User: " + fromBytes(body))
        //Update photos
        ServerMain.photosActor ! ParseByUser(uid)
        //Update videos
        val parsedVideos = new VideosParser().getAllVideos(uid).filter(_.owner_id == uid)
        Videos.saveToDB(parsedVideos)
        channel.basicAck(envelope.getDeliveryTag, false)
      }
    }
    channel.basicQos(1)
    channel.basicConsume(queue, false, consumer)
  }
  def setupPublisherUsersWithTags(channel: Channel, self: ActorRef) {
    val queue = channel.queueDeclare(exchangeUsersWithTags,true,false,false,null).getQueue
    channel.queueBind(queue, exchangeUsersWithTags, "")
    val consumer = new DefaultConsumer(channel) with Logging {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]) {
        val uid = fromBytes(body)
        info("setupPublisherUsersWithTags User: " + fromBytes(body))
        //Update photos
        ServerMain.photosActor ! ParseByUser(uid)
        //Update videos
        val parsedVideos = new VideosParser().getAllVideos(uid).filter(_.owner_id == uid)
        Videos.saveToDB(parsedVideos)
        channel.basicAck(envelope.getDeliveryTag, false)
      }
    }
    channel.basicQos(1)
    channel.basicConsume(queue, false, consumer)
  }

  connection ! CreateChannel(ChannelActor.props(setupPublisherRussians), Some("publisherRussians"))
  (1 to 5).foreach(x => {
    Thread.sleep(Random.nextInt(800))
    connection ! CreateChannel(ChannelActor.props(setupPublisherRussians), Some("publisherRussians" + x))
  })
  connection ! CreateChannel(ChannelActor.props(setupPublisherUsers), Some("publisherUsers"))
  (1 to 5).foreach(x => {
    Thread.sleep(Random.nextInt(800))
    connection ! CreateChannel(ChannelActor.props(setupPublisherUsers), Some("publisherUsers" + x))
  })
  if(isCronServer){
    connection ! CreateChannel(ChannelActor.props(setupPublisherRussiansToUpdate), Some(exchangeRussiansToUpdate))
    (1 to 5).foreach(x => {
      Thread.sleep(Random.nextInt(800))
      connection ! CreateChannel(ChannelActor.props(setupPublisherRussiansToUpdate), Some(exchangeRussiansToUpdate + x))
    })
    connection ! CreateChannel(ChannelActor.props(setupPublisherUsersWithTags), Some(exchangeUsersWithTags))
    (1 to 5).foreach(x => {
      Thread.sleep(Random.nextInt(800))
      connection ! CreateChannel(ChannelActor.props(setupPublisherUsersWithTags), Some(exchangeUsersWithTags + x))
    })
  }

  def fromBytes(x: Array[Byte]) = new String(x, "UTF-8")
  def toBytes(x: Long) = x.toString.getBytes("UTF-8")
  def toBytes(x: String) = x.toString.getBytes("UTF-8")

  val inProgress = new AtomicInteger(0)
}

class Server extends HttpServer {
  override protected def defaultFinatraHttpPort: String = if(ServerMain.isCronServer) { ":8889" } else { ":8888" }
  override val disableAdminHttpServer = true

  override def modules = Seq(Slf4jBridgeModule)

  override def configureHttp(router: HttpRouter) {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add[MainController]
      .add[PhotosController]
      .add[ProfileController]
      .add[TagsAndCitiesController]
      .add[UserController]
      .add[GoogleController]
      .add[CronController]
      .add[VideoController]
      .add[AuthController]
      .add[ArchiveController]
  }
}
