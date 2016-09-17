package com.infonapalm.ridbackend.parsers

import com.infonapalm.ridbackend.Utils.Const
import com.infonapalm.ridbackend.vkStructs._
import org.json4s.DefaultFormats
import org.json4s.JsonAST.{JNull, JValue}
import org.json4s.jackson.JsonMethods._

import scala.util.Try
import scalaj.http.{Http, HttpOptions}

/**
  * Created with IntelliJ IDEA.
  * User: infonapalm
  * Date: 3/30/16
  * Time: 7:50 PM

  */
class VideosParser extends Parser {
  implicit val formats = DefaultFormats
  val URL = "https://api.vk.com/method/video.get.json"

  def getJson(uid: String): JValue = {
    val H = Http(URL).params("owner_id" -> uid, "access_token" -> Const.getRandomToken)
      .option(HttpOptions.connTimeout(10000)).option(HttpOptions.readTimeout(50000))
    System.out.println("URL: " + H.url + "?" + H.params.map( x => x._1 + "=" + x._2 ).mkString("&") )
    val response = H.asString.body.replaceAll("\"response\": ?\\[[0-9]+,?","\"response\": [0")
    if(response.equals("{\"response\":[0]}")){
      JNull
    }else{
      parse(response.replaceAll("\"response\": ?\\[0","\"response\": ["))
    }
  }

  def parseToObjects(json: JValue): List[VideoInfo] = {
    if(json == JNull) return List()
    val errorCode = (json \ "error" \ "error_code").values
    if(errorCode == None) {
      json.extract[VideoResponse].response
    }else if(errorCode.toString.toInt == 6){
      System.out.println(errorCode)
      throw new RetryException()
    }else{
      System.out.println("Strange error code")
      System.out.println(json)
      List()
    }
  }

  def getAllVideos(uid: String): List[VideoInfo] = {
    if(uid.toInt < 0){
      List()
    }else{
      retry(parseToObjects(getJson(uid))).toSet.toList
    }
  }

}
