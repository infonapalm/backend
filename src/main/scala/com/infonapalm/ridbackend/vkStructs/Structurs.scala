package com.infonapalm.ridbackend.vkStructs

import com.infonapalm.ridbackend.{ServerMain}
import com.infonapalm.ridbackend.TemplateStructs.FriendInfoTemplate
import com.infonapalm.ridbackend.models.{Countries, Cities}

import scala.util.Try

/**
 * Created with IntelliJ IDEA.
 * User: infonapalm
 * Date: 4/29/15
 * Time: 10:55 AM

 */
case class Counters(
  albums: String = "0",
  videos: String = "0",
  audios: String = "0",
  notes: String = "0",
  photos: String = "0",
  groups: String = "0",
  friends: String = "0",
  online_friends: String = "0",
  mutual_friends: String = "0",
  user_photos: String = "0",
  user_videos: String = "0",
  followers: String = "0",
  subscriptions: String = "0",
  pages: String = "0"
)
case class MilitaryInfo(unit: String = "", unit_id: String = "", country_id: String = "", from: String = "", until: String = "")
case class Military(military: List[MilitaryInfo] = List())
case class FriendInfo(uid: String,first_name: String="",last_name: String="",domain: String="",photo: String="",
                      user_id: String="", city: Int = 0, country: Int = 0, sex: Int=0,request_from: String = "0",
                      group_id: String = "", is_deleted: String = "false",x: String = "0", y: String = "0",
                      counters: Counters = new Counters(),military: List[MilitaryInfo] = List(),marked: String = "0",
                      comments: String = ""){
  def toTemplate() = {
    val lbl = first_name + " " +
      last_name
    new FriendInfoTemplate(id = uid.toInt, label = lbl, image = photo, is_deleted = is_deleted.toBoolean,
      group = group_id, first_name = first_name, last_name = last_name,
      city = Cities.getCityById(ServerMain.db,city.toString), country = Countries.getCountryById(ServerMain.db,country.toString),
      marked = marked,comments = comments
    )
  }
  override def hashCode(): Int = {
    this.uid.toInt
  }
  override def equals(elem: Any): Boolean = {
    if(elem.isInstanceOf[FriendInfo]){
      this.uid == elem.asInstanceOf[FriendInfo].uid
    }else{
      false
    }
  }
}
case class FriendsResponse(response: List[FriendInfo])

case class Likes(user_likes: String = "0", count: String = "0")
case class PhotosInfo(pid: String,owner_id: String,src: String,src_big: String,created: String,
                      lat: String = "0",long: String = "0",src_xbig: String = "", src_xxbig: String = "",
                      src_xxxbig: String = "",user_group_id: String = "0",tags_count: String = "0", likes: Likes = new Likes())
case class PhotosResponse(response: List[PhotosInfo])

case class PhotosAlbum(aid: String)
case class PhotosAlbumResponse(response: List[PhotosAlbum])

case class CityInfo(cid: String,name: String)
case class CityResponse(response: List[CityInfo])

case class CountryInfo(cid: String,name: String)
case class CountryResponse(response: List[CountryInfo])

case class VideoInfo(vid: String, owner_id: String, image : String, image_medium: String, player: String = "", date: String, title: String = "")
case class VideoResponse(response: List[VideoInfo])

case class UserInfo(id: String = "", username: String = "", pwd: String = "", token: String = "", expiresAt: String = "", role: String = "")

class RetryException extends Exception
