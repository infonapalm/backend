package com.infonapalm.ridbackend.parsers

import com.infonapalm.ridbackend.vkStructs._
import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue
import org.json4s.jackson.JsonMethods._

import scalaj.http.{HttpOptions, Http}

/**
 * Created with IntelliJ IDEA.
 * User: infonapalm
 * Date: 6/17/15
 * Time: 9:17 AM

 */
class CityParser extends Parser {
  val URL = "https://api.vk.com/method/database.getCitiesById.json"

  implicit val formats = DefaultFormats

  def getJson(uid: String): JValue = {
    val H = Http(URL).params("city_ids" -> uid).option(HttpOptions.connTimeout(10000)).option(HttpOptions.readTimeout(50000))
    val response = H.asString.body
    parse(response)
  }

  def parseToObjects(json: JValue): List[CityInfo] = {
    val errorCode = (json \ "error" \ "error_code").values
    if(errorCode == None) {
      json.extract[CityResponse].response
    }else if(errorCode.toString.toInt == 6){
      System.out.println(errorCode)
      throw new RetryException()
    }else{
      System.out.println("Strange error code")
      System.out.println(json)
      List()
    }
  }

  def parseAll(ids: List[String]): List[CityInfo] = {
    ids.grouped(100).map(x => retry(parseToObjects(getJson(x.mkString(","))))).flatten.toList
  }
}
