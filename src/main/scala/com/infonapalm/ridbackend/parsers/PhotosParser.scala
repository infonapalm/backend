package com.infonapalm.ridbackend.parsers

import com.infonapalm.ridbackend.Utils.Const
import com.infonapalm.ridbackend.vkStructs._
import org.json4s.DefaultFormats
import org.json4s.JsonAST.{JNull, JValue}
import org.json4s.jackson.JsonMethods._

import scala.util.matching.Regex
import scala.util.{Random, Try}
import scalaj.http.{HttpOptions, Http}

/**
 * Created with IntelliJ IDEA.
 * User: infonapalm
 * Date: 4/29/15
 * Time: 10:54 AM

 */
class PhotosParser extends Parser{
  implicit val formats = DefaultFormats
  val URL = "https://api.vk.com/method/photos.get.json"
  val URL_ALBUMS = "https://api.vk.com/method/photos.getAlbums.json"
  val URL_SEARCH = "https://api.vk.com/method/photos.search.json"

  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }

  def getJson(uid: String,album_id: String = "wall"): JValue = {
    val H = Http(URL).params("owner_id" -> uid, "album_id" -> album_id, "extended" -> "1", "rev" -> "1", "access_token" -> Const.getRandomToken)
      .option(HttpOptions.connTimeout(10000)).option(HttpOptions.readTimeout(50000))
    System.out.println("URL: " + H.url + "?" + H.params.map( x => x._1 + "=" + x._2 ).mkString("&") )
    val response = H.asString.body
    if(response.equals("{\"response\":[0]}")){
      JNull
    }else{
      parse(response)
    }
  }

  def getJsonAlbums(uid: String): JValue = {
    val H = Http(URL_ALBUMS).params("owner_id" -> uid,"need_system" -> "1", "access_token" -> Const.getRandomToken)
      .option(HttpOptions.connTimeout(10000)).option(HttpOptions.readTimeout(50000))
    System.out.println("URL: " + H.url + "?" + H.params.map( x => x._1 + "=" + x._2 ).mkString("&") )
    val response = H.asString.body
    if(response.equals("{\"response\":[0]}")){
      JNull
    }else{
      parse(response)
    }
  }

  def getJsonSearch(lat: String, lng: String, radius: String = "6000",offset: String = "0",count: String = "100"): JValue = {
    val H = Http(URL_SEARCH).params("lat" -> lat,"long" -> lng, "radius" -> radius, "count" -> count, "sort" -> "0",
      "access_token" -> Const.getRandomToken, "offset" -> offset)
      .option(HttpOptions.connTimeout(10000)).option(HttpOptions.readTimeout(50000))
    System.out.println("URL: " + H.url + "?" + H.params.map( x => x._1 + "=" + x._2 ).mkString("&") )
    val response = H.asString.body.replaceAll("\"response\": ?\\[[0-9]+,?","\"response\": [0")
    if(response.equals("{\"response\": [0]}")){
      JNull
    }else{
      val resp = response.replaceAll("\"response\": ?\\[0","\"response\": [")
      parse(resp)
    }
  }

  def getJsonSearchFrom21112013To01032014(lat: String, lng: String, radius: String = "6000",offset: String = "0",
                                          count: String = "100"): JValue = {
    val H = Http(URL_SEARCH).params("lat" -> lat,"long" -> lng, "radius" -> radius, "count" -> count, "sort" -> "0",
      "access_token" -> Const.getRandomToken, "offset" -> offset, "start_time" -> "1384992000", "end_time" -> "1396310400")
      .option(HttpOptions.connTimeout(10000)).option(HttpOptions.readTimeout(50000))
    System.out.println("URL: " + H.url + "?" + H.params.map( x => x._1 + "=" + x._2 ).mkString("&") )
    val response = H.asString.body.replaceAll("\"response\": ?\\[[0-9]+,?","\"response\": [0")
    if(response.equals("{\"response\": [0]}")){
      JNull
    }else{
      val resp = response.replaceAll("\"response\": ?\\[0","\"response\": [")
      parse(resp)
    }
  }

  def parseToObjects(json: JValue): List[PhotosInfo] = {
    if(json == JNull) return List()
    val errorCode = (json \ "error" \ "error_code").values
    if(errorCode == None) {
      json.extract[PhotosResponse].response
    }else if(errorCode.toString.toInt == 6){
      System.out.println(errorCode)
      throw new RetryException()
    }else{
      System.out.println("Strange error code")
      System.out.println(json)
      List()
    }
  }

  def parseAlbumsToObjects(json: JValue): List[PhotosAlbum] = {
    if(json == JNull) return List()
    val errorCode = (json \ "error" \ "error_code").values
    if(errorCode == None) {
      json.extract[PhotosAlbumResponse].response
    }else if(errorCode.toString.toInt == 6){
      System.out.println(errorCode)
      throw new RetryException()
    }else{
      System.out.println("Strange error code")
      System.out.println(json)
      List()
    }
  }

  def parseSearchToObjects(json: JValue): List[PhotosInfo] = {
    if(json == JNull) return List()
    val errorCode = (json \ "error" \ "error_code").values
    if(errorCode == None) {
      json.extract[PhotosResponse].response
    }else if(errorCode.toString.toInt == 6){
      System.out.println(errorCode)
      throw new RetryException()
    }else{
      System.out.println("Strange error code")
      System.out.println(json)
      List()
    }
  }
  
  def getAllPhotos(uid: String): List[PhotosInfo] = {
    if(uid.toInt < 0){
      List()
    }else{
      retry(parseAlbumsToObjects(getJsonAlbums(uid))).map(x => {
        Try(retry(parseToObjects(getJson(uid, x.aid)))).getOrElse(List())
      }).flatten.toSet.toList
    }
  }

  def getAllPhotosByLatLng(lat: String,lng: String,radius: String="6000"): List[PhotosInfo] = {
    retry(parseSearchToObjects(getJsonSearch(lat,lng,radius)))
  }

  def getAllPhotosByLatLngFrom2014(lat: String,lng: String,radius: String="6000",offset: String = "0",
                                   list: List[PhotosInfo] = List()): List[PhotosInfo] = {
    val result = retry(parseSearchToObjects(getJsonSearch(lat,lng,radius,offset,"1000")))
    if(result.isEmpty){
      list
    }else{
      getAllPhotosByLatLngFrom2014(lat,lng,radius,(offset.toInt+1000).toString,list:::result)
    }
  }

  def getAllPhotosFrom21112013To01032014(lat: String,lng: String,radius: String="6000",offset: String = "0",
                                   list: List[PhotosInfo] = List()): List[PhotosInfo] = {
    val result = retry(parseSearchToObjects(getJsonSearchFrom21112013To01032014(lat,lng,radius,offset,"100")))
    if(result.isEmpty){
      list
    }else{
      getAllPhotosFrom21112013To01032014(lat,lng,radius,(offset.toInt+100).toString,list:::result)
    }
  }
}
