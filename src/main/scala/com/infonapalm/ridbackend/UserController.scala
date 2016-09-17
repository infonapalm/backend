package com.infonapalm.ridbackend

import com.infonapalm.ridbackend.actors.SaveFriendMsg
import com.infonapalm.ridbackend.models.{User, Tags, Friends}
import com.infonapalm.ridbackend.parsers.FriendsParser
import com.rabbitmq.client.Channel
import com.thenewmotion.akka.rabbitmq.ChannelMessage
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.request.{FormParam, RouteParam, RequestInject}
import com.twitter.inject.Logging

/**
  * Created with IntelliJ IDEA.
  * User: infonapalm
  * Date: 1/28/16
  * Time: 8:27 PM

  */
case class PostGetUser(@FormParam link: String = "")
class UserController extends Controller with Logging {
  implicit val database = ServerMain.db

  filter[UserFilter].delete("/user/:userID") { request: Request =>
    Friends.deleteUser(request.getParam("userID",""))
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("deleted" -> "true")).toFuture
  }

  //There is only way to pass link - use POST
  filter[UserFilter].post("/get/user/") { request: PostGetUser =>
    error(request)
    val uid = new FriendsParser().getIdByURI(request.link)
    if(!uid.isEmpty){
      val userInfo = new FriendsParser().getUserInfo(uid)
      Friends.getUserByID(uid) match {
        case Some(x) => response.ok.header("Access-Control-Allow-Origin","*").json(Map("friends" -> List(x.toTemplate())))
        case None => {
          ServerMain.dbActor ! SaveFriendMsg(List(userInfo.head))
          response.ok.header("Access-Control-Allow-Origin","*").json(Map("friends" -> List(userInfo.head.toTemplate())))
        }
      }
    }else{
      response.ok.header("Access-Control-Allow-Origin","*").json(Map("friends" -> Friends.getUserBySearch(request.link).map(_.toTemplate())))
    }
  }

  filter[UserFilter].get("/user/:userID/setAsRussian") { request: Request =>
    def publish(channel: Channel) {
      channel.basicPublish(ServerMain.exchangeRussians, "", null, ServerMain.toBytes(request.getParam("userID","")))
    }
    val publisher = ServerMain.system.actorFor("/user/rabbitmq/publisherRussians")
    publisher ! ChannelMessage(publish, dropIfNoChannel = false)
    Friends.setGroup(request.getParam("userID",""),Friends.RUSSIAN_GROUP_IDX)
    User.logMarked(request.headerMap.getOrElse("X-TKN",""),request.getParam("userID",""))
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("updated" -> "true")).toFuture
  }

  filter[UserFilter].get("/user/:userID/unsetAsRussian") { request: Request =>
    Friends.setGroup(request.getParam("userID",""),Friends.MAIN_GROUP_IDX)
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("updated" -> "true")).toFuture
  }

  post("/user/:userID/tags") { request:Request =>
    Friends.updateTags(request.getParam("userID",""),request.getParam("tags",""))
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("updated" -> true)).toFuture
  }

  filter[UserFilter].get("/user/:userID/tags") { request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("tags" -> Friends.getTags(request.getParam("userID","")))).toFuture
  }

  filter[UserFilter].get("/user/:userID/tags/refresh") { request: Request =>
    val userInfo = new FriendsParser().getUserInfo(request.getParam("userID",""))
    userInfo.headOption match {
      case Some(ui) => {
        ui.military.foreach(x => {
          Tags.appendTag(ui.uid,"в/ч" + x.unit)
          Tags.appendTag(ui.uid,"в/ч" + x.unit + s"(${x.from}-${x.until})")
        })
      }
    }
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("tags" -> Friends.getTags(request.getParam("userID","")))).toFuture
  }
}
