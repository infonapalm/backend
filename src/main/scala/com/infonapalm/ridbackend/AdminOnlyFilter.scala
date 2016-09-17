package com.infonapalm.ridbackend

import com.infonapalm.ridbackend.models.{Cities, User}
import com.twitter.finagle.http.{Status, Response, Request}
import com.twitter.finagle.{Service, Filter}
import com.twitter.util.Future

/**
  * Created with IntelliJ IDEA.
  * User: infonapalm
  * Date: 4/8/16
  * Time: 3:30 PM

  */
class AdminOnlyFilter extends Filter[Request, Response, Request, Response]{
  implicit val database = ServerMain.db

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    Future(Response.apply(Status.Forbidden))
  }
}
