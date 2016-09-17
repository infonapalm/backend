package com.infonapalm.ridbackend.actors

import java.io.{File, FileInputStream, PrintWriter}
import java.net.URL

import akka.actor.Actor
import com.infonapalm.ridbackend.ServerMain
import com.infonapalm.ridbackend.models.Tags
import com.infonapalm.ridbackend.vkStructs.{CityInfo, CityResponse}
import com.twitter.inject.Logging
import sun.misc.BASE64Encoder
import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue
import org.json4s.jackson.JsonMethods._

import scalaj.http.{Http, HttpOptions}
import sys.process._


/**
  * Created by infonapalm on 22/06/16.
  */
case class GoogleVisualMsg(pid: String, owner: String, photo: String,returnResult: Boolean = false)

case class LabelAnnotation(mid: String,description: String,score: String)
case class GoogleResponse(labelAnnotations: List[LabelAnnotation] = List())
case class GoogleVisualResponse(responses: List[GoogleResponse] = List())

class GoogleVisualActor extends Actor with Logging {

  implicit val formats = DefaultFormats

  def receive = {
    case x: GoogleVisualMsg => {
      val imgPath = s"/tmp/${x.pid}_${x.owner}_${System.currentTimeMillis()}.jpg"

      fileDownloader(x.photo, imgPath)

      val file = new File(imgPath)
      val in = new FileInputStream(file)
      val bytes = new Array[Byte](file.length.toInt)
      in.read(bytes)
      in.close()

      val encodedFile = new File(s"${imgPath}.base64")
      val encoded = new BASE64Encoder().encode(bytes).replace("\n", "").replace("\r", "").replace(" ", "+")

      val json =
        s"""{
            |  "requests":[
            |    {
            |      "image":{
            |        "content":"${encoded}"
            |      },
            |      "features":[
            |        {
            |          "type":"LABEL_DETECTION",
            |          "maxResults":5
            |        }
            |      ]
            |    }
            |  ]
            |}
          """.stripMargin.replace(" ","").replace(System.lineSeparator(),"")

      val H = Http("https://vision.googleapis.com/v1/images:annotate?key=")
        .header("Content-Type","application/json").postData(json)
        .option(HttpOptions.connTimeout(10000)).option(HttpOptions.readTimeout(50000))
      val response = H.asString.body

      val jsonResult = parse(response)
      val resultObj = jsonResult.extract[GoogleVisualResponse]

      val tags = resultObj.responses.headOption.getOrElse(new GoogleResponse()).labelAnnotations.map(_.description)

      info(x.photo + " -> " + tags)

      if(x.returnResult){
        sender ! tags
      }else{
        implicit val db = ServerMain.db
        tags.foreach(tag => Tags.appendPhotosTag(x.pid,x.owner,tag))
        Tags.appendPhotosTag(x.pid,x.owner,Tags.GOOGLE_VISUAL_TAG_NAME)
      }
    }
  }

  def parseToObjects(json: JValue): List[CityInfo] = {
    json.extract[CityResponse].response
  }

  def fileDownloader(url: String, filename: String) = {
    new URL(url) #> new File(filename) !!
  }
}
