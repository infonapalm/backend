package com.infonapalm.ridbackend.actors

import akka.actor.Actor
import akka.event.Logging
import com.infonapalm.ridbackend.ServerMain
import com.infonapalm.ridbackend.models.Friends
import com.infonapalm.ridbackend.parsers.{PhotosParser, FriendsParser}
import com.infonapalm.ridbackend.vkStructs.FriendInfo
import com.thenewmotion.akka.rabbitmq._

/**
 * Created with IntelliJ IDEA.
 * User: infonapalm
 * Date: 5/15/15
 * Time: 2:51 PM

 */
class FriendParserActor extends Actor {
  val log = Logging(context.system, this)
  implicit val db = ServerMain.db

  def receive = {
    case x: (String,Boolean) => if(x._2){
      if(!Friends.userWasParsed(x._1)) {
        log.info("Parse in background: " + x._1)
        ServerMain.inProgress.getAndIncrement
        val friends = new FriendsParser().getAllFriends(x._1)
        ServerMain.dbActor ! new SaveFriendMsg(friends)
        ServerMain.inProgress.getAndDecrement
      }
    }else{
      if(!Friends.userWasParsed(x._1)) {
        log.info("Parse in main: " + x._1)
        ServerMain.inProgress.getAndIncrement
        ServerMain.inProgress.getAndDecrement
      }else{
        sender ! Friends.getFriendsByUserID(x._1)
      }
    }
    case x: List[FriendInfo] => {
      x.foreach(fi => {
        if(!Friends.userWasParsed(fi.uid)) {
          log.info("Parse in background: " + fi)
          ServerMain.inProgress.getAndIncrement
          val friends = new FriendsParser().getAllFriends(fi.uid)
          ServerMain.dbActor ! new SaveFriendMsg(friends)
          ServerMain.inProgress.getAndDecrement
        }
      })
    }
  }
}
