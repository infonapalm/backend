package com.infonapalm.ridbackend

import com.infonapalm.ridbackend.Utils.{Geo, GeoConst}
import com.infonapalm.ridbackend.actors.{GetAll, ParseByUser}
import com.infonapalm.ridbackend.models._
import com.infonapalm.ridbackend.parsers._
import com.rabbitmq.client.Channel
import com.thenewmotion.akka.rabbitmq.ChannelMessage
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

/**
  * Created with IntelliJ IDEA.
  * User: infonapalm
  * Date: 3/6/16
  * Time: 11:57 AM

  */
class CronController extends Controller{
  implicit val database = ServerMain.db

  get("/cron/updateDateStringInPhotos") { request: Request =>
    Photos.updateDateStringInPhotos()
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("updated" -> true)).toFuture
  }

  get("/cron/updatePotential") { request: Request =>
    Friends.updatePotential()
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("updated" -> true)).toFuture
  }

  get("/cron/updateRussians") { request: Request =>
    def publish(uid: String)(channel: Channel) {
      channel.basicPublish(ServerMain.exchangeRussiansToUpdate, "", null, ServerMain.toBytes(uid))
    }
    val publisher = ServerMain.system.actorFor("/user/rabbitmq/"+ServerMain.exchangeRussiansToUpdate)
    Friends.getAllRussian().foreach(x => {
      publisher ! ChannelMessage(publish(x.uid), dropIfNoChannel = false)
    })
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("updated" -> true)).toFuture
  }

  get("/cron/updateUsersWithTags") { request: Request =>
    def publish(uid: String)(channel: Channel) {
      channel.basicPublish(ServerMain.exchangeUsersWithTags, "", null, ServerMain.toBytes(uid))
    }
    val publisher = ServerMain.system.actorFor("/user/rabbitmq/"+ServerMain.exchangeUsersWithTags)
    Friends.getUsersWithTags().foreach(uid => {
      publisher ! ChannelMessage(publish(uid), dropIfNoChannel = false)
    })
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("updated" -> true)).toFuture
  }

  get("/cron/polygon/update") { request: Request =>
    val centers = GeoConst.CENTER_POINTS.map(_.split(",") match {
      case Array(lat,lng) => (lat,lng)
    })
    val photos = centers.flatMap(x => {
      new PhotosParser().getAllPhotosByLatLng(x._1, x._2, "6000")
    }).groupBy(_.src).flatMap(_._2).toList.filter(x => Geo.pointInsidePolygon(x.lat.toDouble,x.long.toDouble))
    Photos.saveAllToDB(photos,Photos.TABLE_NAME_POLYGON_MAP)
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("updated" -> true)).toFuture
  }

  get("/cron/polygon/forceUpdate") { request: Request =>
    val centers = GeoConst.CENTER_POINTS.map(_.split(",") match {
      case Array(lat,lng) => (lat,lng)
    })
    centers.foreach(x => ServerMain.photosActor ! new GetAll(x._1,x._2))
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("updated" -> true)).toFuture
  }

  get("/cron/potentialWithPhotosInPolygon"){ request: Request =>
    val photos = Photos.getAllPotentialPhotosWithGeolocation()
    val usersWithPhotosInPolygon = photos.filter(x => Geo.pointInsidePolygon(x.lat.toDouble,x.long.toDouble)).map(_.owner_id).toSet
    Friends.insertPotentialWithPhotosInPolygon(usersWithPhotosInPolygon)
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("updated" -> true)).toFuture
  }

}
