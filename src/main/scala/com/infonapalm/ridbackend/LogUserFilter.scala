package com.infonapalm.ridbackend

import com.infonapalm.ridbackend.models.User
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Filter, Service}
import com.twitter.util.Future

/**
  * Created with IntelliJ IDEA.
  * User: infonapalm
  * Date: 4/8/16
  * Time: 3:30 PM

  */
class LogUserFilter extends Filter[Request, Response, Request, Response]{
  implicit val database = ServerMain.db

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    if(User.couldAccess("1",request.headerMap.getOrElse("X-TKN",""))){
      User.logMarked(request.headerMap.getOrElse("X-TKN",""),request.getParam("url"))
      service(request)
    }else{
      Future(Response.apply(Status.Forbidden))
    }
  }
}
