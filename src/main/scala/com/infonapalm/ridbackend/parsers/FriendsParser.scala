package com.infonapalm.ridbackend.parsers

import com.infonapalm.ridbackend.Utils.Const
import com.infonapalm.ridbackend.vkStructs._
import com.twitter.inject.Logging
import org.json4s.JsonAST.{JValue, JObject, JNothing}

import _root_.scalaj.http.Http
import _root_.scalaj.http.HttpOptions
import scala.io.Source
import scala.util.{Random, Failure, Success, Try}
import org.json4s.DefaultFormats
import org.json4s.Xml._
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._


/**
 * Created with IntelliJ IDEA.
 * User: infonapalm
 * Date: 4/26/15
 * Time: 9:56 AM
  *
  */

// https://api.vk.com/method/friends.get.json?user_id=5143589&fields=city,domain,photo,sex&name_case=nom
class FriendsParser extends Parser with Logging{
  val FEMALE = 1
  val MALE = 2
  val URL = "https://api.vk.com/method/friends.get.json"
  val URL_SEARCH = "https://api.vk.com/method/users.search.json"
  val URL_USER_INFO = "https://api.vk.com/method/users.get.json"

  implicit val formats = DefaultFormats

  def getJson(uid: String): JValue = {
    val H = Http(URL).params("user_id" -> uid,"fields" -> "city,country,domain,photo,sex", "name_case" -> "nom")
      .option(HttpOptions.connTimeout(10000)).option(HttpOptions.readTimeout(50000))
    info("URL: " + H.url + "?" + H.params.map( x => x._1 + "=" + x._2 ).mkString("&") )
    val response = H.asString.body
    parse(response)
  }

  def getJsonUserInfo(uid: String): JValue = {
    Thread.sleep(Random.nextInt(500))
    val H = Http(URL_USER_INFO).params("user_ids" -> uid,"fields" -> "city,country,domain,photo,sex,counters,military",
      "name_case" -> "nom", "access_token" -> Const.getRandomToken)
      .option(HttpOptions.connTimeout(10000)).option(HttpOptions.readTimeout(50000))
    info("URL: " + H.url + "?" + H.params.map( x => x._1 + "=" + x._2 ).mkString("&") )
    val response = H.asString.body
    if(response == "{\"response\":[0]}"){
      JNull
    }else{
      parse(response)
    }
  }

  def getJsonUserSearch(domain: String): JValue = {
    val H = Http(URL_SEARCH).params("q" -> domain, "access_token" -> Const.getRandomToken)
      .option(HttpOptions.connTimeout(10000)).option(HttpOptions.readTimeout(50000))
    info("URL: " + H.url + "?" + H.params.map( x => x._1 + "=" + x._2 ).mkString("&") )
    val response = H.asString.body.replaceAll("\"response\": ?\\[[0-9]+,","\"response\": [")
    if(response == "{\"response\":[0]}" || response.matches("\\{\"response\":\\[[0-9]+\\]\\}")){
      JNull
    }else{
      parse(response)
    }
  }
  
  def parseToObjects(uid: String,json: JValue): List[FriendInfo] = {
    val errorCode = (json \ "error" \ "error_code").values
    if(errorCode == None) {
      json.extract[FriendsResponse].response.map(x => x.copy(request_from = uid))
    }else if(errorCode.toString.toInt == 6){
      info(errorCode)
      throw new RetryException()
    }else{
      info("Strange error code")
      info(json)
      List()
    }
  }

  def getMaleFriends(uid: String): List[FriendInfo] = getAllFriends(uid).filter(_.sex == MALE)
  
  def getAllFriends(uid: String): List[FriendInfo] = {
    if(uid.toInt < 0){
      List()
    }else{
      retry(parseToObjects(uid,getJson(uid)))
    }
  }

  def getUserInfo(uid: String): List[FriendInfo] = {
    if(uid.toInt < 0){
      List()
    }else{
      retry(parseToObjects(uid,getJsonUserInfo(uid)))
    }
  }

  def getUsersInfo(uid: List[String]): List[FriendInfo] = {
    uid.flatMap(x => {
      Thread.sleep(100)
      if(x.toInt < 0){
        List()
      }else{
        retry(parseToObjects("",getJsonUserInfo(x)))
      }
    })
  }

  def getUserSearch(domain: String): Option[FriendInfo] = {
    val json = getJsonUserSearch(domain)
    if(json == JNull){
      retry(parseToObjects("",getJsonUserInfo(domain))).headOption
    }else{
      retry(parseToObjects("",json)).headOption
    }
  }

  def getIdByURI(link: String): String = {
    getUserByURI(link) match {
      case Some(user) => user.uid
      case None => ""
    }
  }

  def getUserByURI(link: String): Option[FriendInfo] ={
    if(link.contains("/id")){
      val patternById = "vk.com/id(\\d+)".r
      info("By id: " + patternById.findFirstMatchIn(link).get.group(1))
      getUserInfo(patternById.findFirstMatchIn(link).get.group(1)).headOption
    }else if(link.contains("vk.com")){
      val patternByNickname = "vk.com/(.*)".r
      info("By nickname: " + patternByNickname.findFirstMatchIn(link).get.group(1))
      getUserSearch(patternByNickname.findFirstMatchIn(link).get.group(1))
    }else{
      None
    }
  }
}
