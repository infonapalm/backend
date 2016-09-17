package com.infonapalm.ridbackend

import com.infonapalm.ridbackend.models.User
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.request._

/**
  * Created with IntelliJ IDEA.
  * User: infonapalm
  * Date: 4/5/16
  * Time: 5:23 PM

  */
case class AuthRequest(@FormParam login: String,@FormParam password: String,@FormParam ip: String="")
class AuthController extends Controller{
  implicit val database = ServerMain.db

  post("/auth/"){ request: AuthRequest =>
    val userInfo = User.auth(request.login,request.password)
    if(!userInfo.id.isEmpty){
      User.logAuth(userInfo.token,request.ip)
      response.ok.header("Access-Control-Allow-Origin","*").json(Map("result" -> true, "token" -> userInfo.token, "role" -> userInfo.role)).toFuture
    }else{
      User.logFailedAuth(request.login,request.password,"",request.ip)
      response.ok.header("Access-Control-Allow-Origin","*").json(Map("result" -> false)).toFuture
    }
  }

  get("/auth/:token"){ request: Request =>
    val userInfo = User.auth(request.getParam("token"))
    if(userInfo.isDefined){
      User.logAuth(request.getParam("token"),request.getParam("ip"))
      response.ok.header("Access-Control-Allow-Origin","*").json(Map("result" -> true, "token" -> userInfo.get.token, "role" -> userInfo.get.role)).toFuture
    }else{
      User.logFailedAuth("","",request.getParam("token"),request.getParam("ip"))
      response.ok.header("Access-Control-Allow-Origin","*").json(Map("result" -> false)).toFuture
    }
  }

}
