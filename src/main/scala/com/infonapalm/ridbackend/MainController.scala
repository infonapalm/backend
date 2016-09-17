package com.infonapalm.ridbackend

import java.io.File
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.Date

import com.infonapalm.ridbackend.actors.{SaveFriendMsg, SavePhotoMsg}
import com.infonapalm.ridbackend.models.{Cities, Countries, Friends, User}
import com.infonapalm.ridbackend.parsers.{CityParser, CountryParser, FriendsParser, PhotosParser}
import com.mchange.v2.c3p0.ComboPooledDataSource
import com.rabbitmq.client.Channel
import com.thenewmotion.akka.rabbitmq.ChannelMessage
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.request.{FormParam, Header, RouteParam}
import com.twitter.io.Files
import org.apache.commons.io.FileUtils
import org.json4s.jackson.JsonMethods._

import scala.slick.jdbc.JdbcBackend._
import scala.util.Try

case class PostStartParsingRequestURL(@FormParam url: String = "")
case class PostStartParsingRequestURLWithTagAndComment(@FormParam url: String = "",@FormParam tag: String = "",@FormParam comment: String = "")
class MainController extends Controller {

  implicit val db = ServerMain.db

  options("/:*"){ request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*")
      .header("Access-Control-Allow-Methods","POST, GET, OPTIONS, PUT, DELETE")
      .header("Access-Control-Allow-Headers","Content-Type")
      .header("Access-Control-Allow-Headers","X-TKN")
      .toFuture
  }

  filter[UserFilter].get("/getFriends") { request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("friends" -> List())).toFuture
  }

  filter[UserFilter].get("/getPotential") { request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("friends" -> Friends.getPotential().map(_.toTemplate()))).toFuture
  }

  filter[UserFilter].get("/getRussians") { request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("friends" -> Friends.getAllRussian().map(_.toTemplate()))).toFuture
  }

  filter[LogUserFilter].post("/startParsing"){ request: PostStartParsingRequestURL =>
    if(request.url.isEmpty){
      response.ok.header("Access-Control-Allow-Origin","*").json(Map()).toFuture
    }else{
      val userID = new FriendsParser().getIdByURI(request.url)
      def publish(channel: Channel) {
        channel.basicPublish(ServerMain.exchangeRussians, "", null, ServerMain.toBytes(userID))
      }
      val publisher = ServerMain.system.actorFor("/user/rabbitmq/publisherRussians")
      publisher ! ChannelMessage(publish, dropIfNoChannel = false)
      response.ok.header("Access-Control-Allow-Origin","*").json(Map("parsed" -> "true","uid" -> userID)).toFuture
    }
  }

  filter[UserFilter].get("/startParsing/:userID"){ request: Request  =>
    if(request.getParam("userID","").isEmpty){
      response.ok.header("Access-Control-Allow-Origin","*").json(Map()).toFuture
    }else{
      val userID = new FriendsParser().getIdByURI("http://vk.com/id" + request.getParam("userID",""))
      def publish(channel: Channel) {
        channel.basicPublish(ServerMain.exchangeRussians, "", null, ServerMain.toBytes(request.getParam("userID","")))
      }
      val publisher = ServerMain.system.actorFor("/user/rabbitmq/publisherRussians")
      publisher ! ChannelMessage(publish, dropIfNoChannel = false)
      response.ok.header("Access-Control-Allow-Origin","*").json(Map("parsed" -> "true","uid" -> request.getParam("userID",""))).toFuture
    }
  }

  post("/addUsersWithTagAndComment"){ request: PostStartParsingRequestURLWithTagAndComment =>
    println(request)
    def publish(channel: Channel) {
      channel.basicPublish(ServerMain.exchangeRussians, "", null, ServerMain.toBytes(request.url + "~" + request.tag + "~" + request.comment))
    }
    val publisher = ServerMain.system.actorFor("/user/rabbitmq/publisherRussians")
    publisher ! ChannelMessage(publish, dropIfNoChannel = false)
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("parsed" -> "true","uid" -> request.url)).toFuture
  }

  def fromBytes(x: Array[Byte]) = new String(x, "UTF-8")

}
