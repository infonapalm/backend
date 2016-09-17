package com.infonapalm.ridbackend.actors

import akka.actor.Actor
import akka.event.Logging
import com.infonapalm.ridbackend.ServerMain
import com.infonapalm.ridbackend.models.{Videos, Photos, Friends}
import com.infonapalm.ridbackend.vkStructs.{VideoInfo, PhotosInfo, FriendInfo}

/**
 * Created with IntelliJ IDEA.
 * User: infonapalm
 * Date: 5/15/15
 * Time: 3:13 PM

 */
case class SaveFriendMsg(data: List[FriendInfo])
case class SavePhotoMsg(data: List[PhotosInfo])
case class SaveVideoMsg(data: List[VideoInfo])

class DBActor extends Actor{
  val log = Logging(context.system, this)

  implicit val db = ServerMain.db

  def receive = {
    case x: SaveFriendMsg => {
      log.info("SAVING. Size to save friends: " + x.data.length)
      x.data.foreach(Friends.saveToDB(_))
    }
    case x: SavePhotoMsg => {
      log.info("SAVING. Size to save photos: " + x.data.length)
      Photos.saveAllToDB(x.data)
      Photos.updatePhotosLikes(x.data)
    }
    case x: SaveVideoMsg => {
      log.info("SAVING. Size to save videos: " + x.data.length)
      Videos.saveToDB(x.data)
    }
    case x => System.out.println(x)
  }
}
