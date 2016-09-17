package com.infonapalm.ridbackend

import java.net.URLDecoder

import com.infonapalm.ridbackend.models.{User, Cities, Tags, Friends}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.request.{QueryParam, FormParam, RouteParam}

/**
  * Created with IntelliJ IDEA.
  * User: infonapalm
  * Date: 1/28/16
  * Time: 8:17 PM

  */
case class PostTagsStatistics(@FormParam tag: String = "")
case class PostCitiesStatistics(@FormParam city: String = "")
case class GetTags(@QueryParam term: String = "")
class TagsAndCitiesController extends Controller{
  implicit val database = ServerMain.db

  filter[AdminOnlyFilter].get("/tags/:tag") { request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(
      Map("friends" -> Friends.getUsersByTag(URLDecoder.decode(request.getParam("tag",""))).map(_.toTemplate()))
    ).toFuture
  }

  //Must be after /tags/:tag to rewrite
  filter[AdminOnlyFilter].get("/tags/statistics") { request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("statistics" -> Tags.getTagsStatistics())).toFuture
  }

  filter[AdminOnlyFilter].post("/tags/statistics") { request: PostTagsStatistics =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("statistics" -> Tags.getTagsStatistics(request.tag))).toFuture
  }

  filter[AdminOnlyFilter].get("/cities/:cityID") { request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("friends" -> Friends.getUsersByCityIDs(request.getParam("cityID","")).map(_.toTemplate()))).toFuture
  }

  //Must be after /cities/:cityID to rewrite
  filter[AdminOnlyFilter].get("/cities/statistics") { request: Request =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("statistics" -> Cities.getStatistics())).toFuture
  }

  filter[AdminOnlyFilter].post("/cities/statistics") { request: PostCitiesStatistics =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Map("statistics" -> Cities.getStatistics(request.city))).toFuture
  }

  get("/tags/") { request: GetTags =>
    response.ok.header("Access-Control-Allow-Origin","*").json(Tags.getTagsForAutocomplete(request.term).filter(!_._2.startsWith("в/ч")).map(
      x => Map(
        "id" -> x._1,
        "label" -> x._2,
        "value" -> x._2
      )
    )).toFuture
  }

}
