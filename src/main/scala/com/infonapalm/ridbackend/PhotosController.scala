package com.infonapalm.ridbackend

import java.net.URLDecoder

import com.infonapalm.ridbackend.Utils.{Geo, GeoConst}
import com.infonapalm.ridbackend.actors.{GetAll, SavePhotoMsg}
import com.infonapalm.ridbackend.models.{Friends, Photos, Tags}
import com.infonapalm.ridbackend.parsers.PhotosParser
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.request.{FormParam, QueryParam, RouteParam}

import scala.collection.immutable.TreeMap

/**
  * Created with IntelliJ IDEA.
  * User: infonapalm
  * Date: 1/28/16
  * Time: 6:49 PM

  */
case class PostPhotosSearch(@FormParam photo: String = "")
case class PhotosDelete(@FormParam photos: String = "")
class PhotosController extends Controller{
  implicit val database = ServerMain.db

  //URLS for 2 parameters
  filter[AdminOnlyFilter].post("/photos/delete/"){ request: PhotosDelete =>
    val photos = request.photos.split(",").map(str => {
      str.split("\\|") match {
        case Array(pid, uid) => (pid, uid)
      }
    })
    Photos.deleteAllPhotos(photos.toList,Photos.TABLE_NAME_POLYGON_MAP)
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("deleted" -> true)).toFuture
  }

  filter[UserFilter].post("/photos/search") { request:PostPhotosSearch =>
    val image = Photos.search(request.photo)
    image match {
      case Some(i) => response.ok.header("Access-Control-Allow-Origin","*").json(Map("images" -> List(i))).toFuture
      case None => response.ok.header("Access-Control-Allow-Origin","*").json(Map("images" -> List())).toFuture
    }
  }

  filter[UserFilter].get("/photos/:userID") { request: Request =>
    val photos = Photos.getPhotoByUserID(request.getParam("userID",""))
    if(photos.isEmpty){
      val parsedPhotos = new PhotosParser().getAllPhotos(request.getParam("userID",""))
      System.out.println("Get photos from site")
      ServerMain.dbActor ! SavePhotoMsg(parsedPhotos)
      response.ok.header("Access-Control-Allow-Origin","*").json(Map("photos" -> parsedPhotos.sortBy(- _.created.toInt))).toFuture
    }else{
      System.out.println("Get photos from cache")
      response.ok.header("Access-Control-Allow-Origin","*").json(Map("photos" -> photos.sortBy(- _.created.toInt))).toFuture
    }
  }

  //URLS for 3 parameters

  filter[UserFilter].get("/photos/:userID/force") { request:Request =>
    val parsedPhotos = new PhotosParser().getAllPhotos(request.getParam("userID",""))
    Photos.saveAllToDB(parsedPhotos)
    Photos.updatePhotosLikes(parsedPhotos)
    val photos = Photos.getPhotoByUserID(request.getParam("userID",""))
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("photos" -> photos.sortBy(- _.created.toInt))).toFuture
  }

  filter[UserFilter].get("/photos/polygon/statistics"){ request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(
      Photos.getPolygonDatesWithCounts().foldLeft(List[Map[String,String]]())((a,b) => a ::: List(Map("date" -> b._1, "count" -> b._2)))
    )
  }

  filter[AdminOnlyFilter].get("/photos/polygon/count"){ request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("count" -> Photos.getPolygonTotal())).toFuture
  }

  filter[AdminOnlyFilter].get("/photos/polygon/:offset") { request:Request =>
    val photos = Photos.getPhotosByPolygon(
      Photos.POLYGON_LIMIT,request.getParam("offset","0").toInt
    ).sortBy(_.created)
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("photos" -> photos)).toFuture
  }

  filter[AdminOnlyFilter].get("/photos/byDate/count") { request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("count" -> Photos.getRussiansPhotosByDateTotal())).toFuture
  }

  filter[AdminOnlyFilter].get("/photos/byDate/:dateOffset") { request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("photos" -> Photos.getRussiansPhotosByDate(request.getParam("dateOffset","0").toInt))).toFuture
  }

  filter[AdminOnlyFilter].delete("/photos/:pid/:userID") { request:Request =>
    Photos.deletePhoto(request.getParam("pid",""),request.getParam("userID",""))
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("deleted" -> true)).toFuture
  }

  filter[UserFilter].get("/photos/:lat/:lng") { request: Request =>
    val photos = new PhotosParser().getAllPhotosByLatLng(
      request.getParam("lat",""),request.getParam("lng","")
    )
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("photos" -> photos)).toFuture
  }


  // URLS for 4 parameters
  filter[AdminOnlyFilter].get("/photos/byTags/:tag/count") { request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(
      Map("count" -> Photos.getRussiansPhotosByTagTotal(URLDecoder.decode(request.getParam("tag",""),"UTF-8")))
    ).toFuture
  }

  filter[AdminOnlyFilter].get("/photos/byTags/:tag/:dateOffset") { request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(
      Map("photos" -> Photos.getRussainsPhotosByTag(URLDecoder.decode(request.getParam("tag",""),"UTF-8"),request.getParam("dateOffset","0").toInt))
    ).toFuture
  }

  filter[AdminOnlyFilter].get("/photos/polygon/:offsetDate/count"){ request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(
      Map("count" -> Photos.getPolygonByDateTotal(request.getParam("offsetDate","0").toInt).toInt)
    ).toFuture
  }

  filter[AdminOnlyFilter].get("/photos/polygon/:offsetDate/:offset") { request:Request =>
    val photos = Photos.getPhotosByPolygon(
      Photos.POLYGON_LIMIT,request.getParam("offsetDate","").toInt,request.getParam("offset","").toInt
    ).sortBy(_.created)
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("photos" -> photos)).toFuture
  }

  filter[AdminOnlyFilter].delete("/photos/polygon/:pid/:userID") { request: Request =>
    Photos.deletePhoto(request.getParam("pid",""),request.getParam("userID",""),Photos.TABLE_NAME_POLYGON_MAP)
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("deleted" -> true)).toFuture
  }

  filter[UserFilter].get("/photos/tags/:pid/:uid"){ request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("tags" -> Tags.getPhotosTags(
      request.getParam("pid",""),request.getParam("uid","")
    ))).toFuture
  }

  filter[UserFilter].get("/photos/:lat/:lng/:radius") { request:Request =>
    val photos = new PhotosParser().getAllPhotosByLatLng(request.getParam("lat",""),
      request.getParam("lng",""), request.getParam("radius",""))
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("photos" -> photos)).toFuture
  }

  post("/photos/tags/:pid/:uid"){ request: Request =>
    Tags.savePhotosTags(request.getParam("pid",""),request.getParam("uid",""),request.getParam("tags",""))
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("updated" -> true)).toFuture
  }
}
