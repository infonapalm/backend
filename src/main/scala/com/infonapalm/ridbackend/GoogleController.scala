package com.infonapalm.ridbackend

import com.google.common.io.BaseEncoding
import com.infonapalm.ridbackend.models.{Friends, Photos, Tags}
import com.infonapalm.ridbackend.parsers.PhotosParser
import com.infonapalm.ridbackend.vkStructs.{CityInfo, CityResponse, RetryException}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import org.apache.commons.codec.binary.Base64
import org.json4s.JsonAST.JValue
import sun.misc.BASE64Encoder

import scala.io.Source
import scalaj.http.Http
import sys.process._
import java.net.URL
import java.io.{File, FileInputStream, FileOutputStream, PrintWriter}

import com.infonapalm.ridbackend.actors.GoogleVisualMsg
import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue
import org.json4s.jackson.JsonMethods._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created with IntelliJ IDEA.
  * User: infonapalm
  * Date: 2/26/16
  * Time: 8:11 PM
  *
  */
class GoogleController extends Controller {

  implicit val formats = DefaultFormats

  implicit val db = ServerMain.db

  implicit val timeout = Timeout(60 seconds)

  get("/google/visual/maybe"){ request: Request =>
    val potential = Friends.getPotentialForGoogleVisual("500")
    val photos = potential.par.map(user => {
      Photos.getPhotoForVisualRecognition(user.uid)
    })

    val tags = photos.map( photo => {
      if(!photo._1.isEmpty){
        ServerMain.googleVisualActor ? GoogleVisualMsg(photo._1,photo._2,photo._3)
      }else{
        if(photo._1.isEmpty){
          Tags.appendTag(photo._2,Tags.NO_PHOTOS_FOR_TAG_NAME)
          Tags.appendTag(photo._2,Tags.GOOGLE_VISUAL_TAG_NAME)
        }else{
          Tags.appendPhotosTag(photo._1,photo._2,Tags.GOOGLE_VISUAL_TAG_NAME)
        }
      }

//      val tagList = ServerMain.googleVisualActor ? GoogleVisualMsg(photo._1,photo._2,photo._3)
//      (photo._3,tagList)
    })

//    val body = tags.map(x => {
//      val tagsStr = Await.result(x._2, timeout.duration).asInstanceOf[List[String]].mkString(",")
//      s"""
//         |<img src="${x._1}" width="300" alt="${tagsStr}" title="${tagsStr}" />
//       """.stripMargin
//    }).mkString("")
//    response.ok.header("Access-Control-Allow-Origin","*").html(body).toFuture

    response.ok.header("Access-Control-Allow-Origin","*").json(Map("updated" -> true)).toFuture
  }

  def parseToObjects(json: JValue): List[CityInfo] = {
    json.extract[CityResponse].response
  }

  get("/google/visio/likes"){ request: Request =>
    val potential = Friends.getPotentialForGoogleVisual("100000")
    potential.drop(1060).foreach(user => {
      val parsedPhotos = new PhotosParser().getAllPhotos(user.uid).filter(_.likes.count.toInt > 0)
      Photos.saveAllToDB(parsedPhotos)
      Photos.updatePhotosLikes(parsedPhotos)
    })
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("updated" -> true)).toFuture
  }

  def fileDownloader(url: String, filename: String) = {
    new URL(url) #> new File(filename) !!
  }
}


case class LabelAnnotation(mid: String,description: String,score: String)
case class GoogleResponse(labelAnnotations: List[LabelAnnotation] = List())
case class GoogleVisualResponse(responses: List[GoogleResponse] = List())
