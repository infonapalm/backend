package com.infonapalm.ridbackend

import com.infonapalm.ridbackend.actors.{SaveVideoMsg, SavePhotoMsg}
import com.infonapalm.ridbackend.models.{Photos, Videos}
import com.infonapalm.ridbackend.parsers.VideosParser
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.request._

/**
  * Created with IntelliJ IDEA.
  * User: infonapalm
  * Date: 3/30/16
  * Time: 7:47 PM

  */
case class VideosDelete(@FormParam videos: String = "")
class VideoController extends Controller{
  implicit val database = ServerMain.db

  filter[UserFilter].post("/videos/delete"){ request: VideosDelete =>
    val videos = request.videos.split(",").map(str => {
      str.split("\\|") match {
        case Array(vid, uid) => (vid, uid)
      }
    })
    Videos.deleteAllVideos(videos.toList)
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("deleted" -> true)).toFuture
  }

  filter[UserFilter].get("/videos/:userID"){ request: Request =>
    val videos = Videos.getAllVideosForUser(request.getParam("userID"))
    if(videos.isEmpty){
      val parsedVideos = new VideosParser().getAllVideos(request.getParam("userID")).filter(_.owner_id == request.getParam("userID"))
      ServerMain.dbActor ! SaveVideoMsg(parsedVideos)
      response.ok.header("Access-Control-Allow-Origin","*").json(Map("videos" -> parsedVideos.sortBy(- _.date.toInt))).toFuture
    }else{
      response.ok.header("Access-Control-Allow-Origin","*").json(Map("videos" -> videos.sortBy(- _.date.toInt))).toFuture
    }
  }

  filter[UserFilter].get("/videos/byDate/count"){ request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("count" -> Videos.getRussiansVideosByDateTotal())).toFuture
  }

  filter[UserFilter].get("/videos/byDate/:offset"){ request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("videos" -> Videos.getRussiansVideosByDate(request.getParam("offset","0").toInt))).toFuture
  }

  filter[UserFilter].delete("/videos/:vid/:userID"){ request: Request =>
    Videos.deleteVideo(request.getParam("pid",""),request.getParam("userID",""))
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("deleted" -> true)).toFuture
  }

}
