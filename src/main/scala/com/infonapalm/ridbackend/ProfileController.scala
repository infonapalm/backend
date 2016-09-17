package com.infonapalm.ridbackend

import com.infonapalm.ridbackend.models.{Photos, Friends}
import com.infonapalm.ridbackend.parsers.FriendsParser
import com.infonapalm.ridbackend.vkStructs.FriendInfo
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.request.{RouteParam, FormParam}

/**
  * Created with IntelliJ IDEA.
  * User: infonapalm
  * Date: 1/28/16
  * Time: 7:51 PM

  */
class ProfileController extends Controller{
  implicit val database = ServerMain.db
  val groupInfo = FriendInfo(uid = "-1",first_name = "Group")

  filter[UserFilter].get("/profile/:userID") { request:Request =>
    val userInfo = Friends.getUserByID(request.getParam("userID","")) match {
      case Some(x) => Some(x)
      case None => {
        val parsedUserInfo = new FriendsParser().getUserInfo(request.getParam("userID","")).headOption
        parsedUserInfo match {
          case Some(x) => {
            Friends.saveToDB(x)
            Some(x)
          }
          case None => None
        }
      }
    }

    userInfo match {
      case Some(x) => response.ok.header("Access-Control-Allow-Origin","*").json(Map("user" -> x.toTemplate())).toFuture
      case None => response.ok.header("Access-Control-Allow-Origin","*").json(Map("user" -> groupInfo.toTemplate())).toFuture
    }
  }

  filter[UserFilter].get("/profile/friends1stRound/:userID") { request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map(
      "friends1stRound" -> Friends.getRussianFriends(request.getParam("userID","")).map(_.toTemplate()))
    ).toFuture
  }

  filter[UserFilter].get("/profile/friends2ndRound/:userID") { request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map(
      "friends2ndRound" -> Friends.getRussianFriends2ndRound(request.getParam("userID","")).map(_.toTemplate()))
    ).toFuture
  }

  filter[UserFilter].get("/profile/potential/:userID") { request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map(
      "potential" -> Friends.getPotential(request.getParam("userID","")).map(_.toTemplate()))
    ).toFuture
  }

  filter[UserFilter].get("/profile/usersWithSimilarTags/:userID") { request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map(
      "usersWithSimilarTags" -> Friends.getUsersWithSimilarTagToUser(request.getParam("userID","")).map(_.toTemplate())
    )).toFuture
  }

  filter[UserFilter].get("/profile/photosLatLng/:userID") { request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map(
      "photosLatLng" -> Photos.getLatLngForUsers(List(new FriendInfo(uid = request.getParam("userID",""))))
    ) ).toFuture
  }

  filter[UserFilter].get("/profile/photosLatLng/:userID/friends1stRound") { request: Request =>
    val friends1stRound = Friends.getRussianFriends(request.getParam("userID",""))
    response.ok.header("Access-Control-Allow-Origin","*").json(Map(
      "photosLatLng" -> Photos.getLatLngForUsers(friends1stRound)
    )).toFuture;
  }

  filter[UserFilter].get("/profile/photosLatLng/:userID/friends2ndRound") { request: Request =>
    val friends2ndRound = Friends.getRussianFriends2ndRound(request.getParam("userID",""))
    response.ok.header("Access-Control-Allow-Origin","*").json(Map(
      "photosLatLng" -> Photos.getLatLngForUsers(friends2ndRound)
    )).toFuture;
  }

  filter[UserFilter].post("/profile/updateComments/:userID") { request: Request =>
    Friends.updateComments(request.getParam("userID",""),request.getParam("comments",""))
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("updated"->true)).toFuture
  }
}
