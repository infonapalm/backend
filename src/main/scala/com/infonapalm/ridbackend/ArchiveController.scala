package com.infonapalm.ridbackend

import java.io.{File, FileOutputStream}

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.request._
import com.ui4j.api.browser.BrowserFactory
import sys.process._

/**
  * Created by infonapalm on 01/05/16.
  */
case class PostArchiveRequestURL(@FormParam url: String = "")
class ArchiveController extends Controller {
  get("/archive/"){ request: Request =>
    val resDownload = "wget -E -H -k -K -p -e robots=off -P /tmp/3037801_411764529 http://m.vk.com/photo3037801_411764529?rev=1&post=3037801_982&from=profile" !

    val resArchive = "tar -zcvf 3037801_411764529.tar.gz 3037801_411764529/" !

    response.ok.header("Access-Control-Allow-Origin","*").json(Map("download" -> resDownload, "archived" -> resArchive)).toFuture
  }
}