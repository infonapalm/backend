package com.infonapalm.ridbackend.actors

import akka.actor.Actor
import akka.event.Logging
import com.infonapalm.ridbackend.{ServerMain}
import com.infonapalm.ridbackend.Utils.Geo
import com.infonapalm.ridbackend.models.{Photos, Friends}
import com.infonapalm.ridbackend.parsers.{PhotosParser, FriendsParser}
import com.infonapalm.ridbackend.vkStructs.FriendInfo

/**
 * Created with IntelliJ IDEA.
 * User: infonapalm
 * Date: 5/15/15
 * Time: 2:51 PM

 */
case class GetAll(lat: String,lon: String)
case class GetNew(lat: String,lon: String)
case class ParseByUser(uid: String)
class PhotosParserActor extends Actor {
  val log = Logging(context.system, this)
  implicit val db = ServerMain.db

  def receive = {
    case x: GetAll => {
      val photos = new PhotosParser().getAllPhotosByLatLngFrom2014(x.lat, x.lon, "5000")
      val photosFiltered = photos.filter(x => Geo.pointInsidePolygon(x.lat.toDouble,x.long.toDouble))
      Photos.saveAllToDB(photosFiltered,Photos.TABLE_NAME_POLYGON_MAP)
    }
    case x: GetNew => {
      val photos = new PhotosParser().getAllPhotosByLatLng(x.lat, x.lon, "5000")
      val photosFiltered = photos.filter(x => Geo.pointInsidePolygon(x.lat.toDouble,x.long.toDouble))
      Photos.saveAllToDB(photosFiltered,Photos.TABLE_NAME_POLYGON_MAP)
    }
    case x: ParseByUser => {
      val parsedPhotos = new PhotosParser().getAllPhotos(x.uid)
      Photos.saveAllToDB(parsedPhotos)
      Photos.updatePhotosLikes(parsedPhotos)
    }
  }
}
