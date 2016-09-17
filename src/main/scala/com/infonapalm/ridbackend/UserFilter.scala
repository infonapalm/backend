package com.infonapalm.ridbackend

import com.infonapalm.ridbackend.models.User
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Filter, Service}
import com.twitter.util.Future

import scala.util.Try

/**
  * Created with IntelliJ IDEA.
  * User: infonapalm
  * Date: 4/8/16
  * Time: 3:30 PM

  */
class UserFilter extends Filter[Request, Response, Request, Response]{
  implicit val database = ServerMain.db

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    Future(Response.apply(Status.Forbidden))
  }
}
