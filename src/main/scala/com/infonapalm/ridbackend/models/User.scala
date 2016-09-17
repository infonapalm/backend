package com.infonapalm.ridbackend.models

import java.security.MessageDigest

import com.infonapalm.ridbackend.Utils.BearerTokenGenerator
import com.infonapalm.ridbackend.vkStructs.UserInfo

import scala.slick.jdbc.{StaticQuery => Q}
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

/**
  * Created with IntelliJ IDEA.
  * User: infonapalm
  * Date: 4/5/16
  * Time: 4:59 PM

  */
object User {
  val ADMIN_ROLE = 100
  val NO_ROLE = 0
  val USER_ROLE = 1

  val TABLE_NAME = "users"
  val USER_AUTH_IPS_TABLE_NAME = "user_auth_ips"
  val USER_FAILED_AUTH_IPS_TABLE_NAME = "user_failed_auth_ips"
  val USER_MARKED_LOG_TABLE_NAME = "user_marked_log"
  val FIELDS = "id,username,pwd,token,expired_at,role"

  implicit def tuple6ToUser(o: Option[(String,String,String,String,String,String)]): Option[UserInfo] = o.map(x => UserInfo(x._1,x._2,x._3,x._4,x._5,x._6))

  def getUserByToken(token: String)(implicit db: Database): Option[UserInfo] ={
    val q = Q[String,(String,String,String,String,String,String)] +
      s"""
         |SELECT $FIELDS
         |FROM $TABLE_NAME
         |WHERE token = ? AND expired_at > NOW()
       """.stripMargin

    q(token).list.headOption
  }

  def auth(username: String, password: String)(implicit db: Database): UserInfo = db.withDynSession{
    val q = Q[(String,String),(String,String,String,String,String,String)] +
      s"""
         |SELECT $FIELDS
         |FROM $TABLE_NAME
         |WHERE username = ? AND pwd = md5(concat('RiD',?,'RiD'))
       """.stripMargin

    val user = tuple6ToUser(q(username,password).list.headOption)

    if(user.isDefined){
      val token = new BearerTokenGenerator().generateMD5Token("")
      val interval = user.get.role match {
        case "1" => "30 MINUTES"
        case "100" => "1 DAY"
        case _ => "1 DAY"
      }
      (Q.u + s"UPDATE $TABLE_NAME SET token = '$token' , expired_at = NOW() + INTERVAL $interval WHERE id = ${user.get.id}").execute
      user.get.copy(token = token)
    }else{
      new UserInfo()
    }
  }

  def auth(token: String)(implicit db: Database): Option[UserInfo] = db.withDynSession{
    val q = Q[String,(String,String,String,String,String,String)] +
      s"""
         |SELECT $FIELDS
         |FROM $TABLE_NAME
         |WHERE token = ? AND expired_at > NOW()
       """.stripMargin

    q(token).list.headOption
  }

  def md5(s: String): String = {
    MessageDigest.getInstance("MD5").digest(("RiD"+s+"RiD").getBytes).map("%02X".format(_)).mkString.toLowerCase
  }

  def couldAccess(role: String,token: String)(implicit db: Database): Boolean = db.withDynSession{
    val q = Q[String,String] +
      s"""
         |SELECT role
         |FROM $TABLE_NAME
         |WHERE token = ? and expired_at > NOW()
       """.stripMargin

    q(token).list.headOption.getOrElse("0").toInt >= role.toInt
  }

  def logMarked(token: String,link: String)(implicit db: Database): Unit = db.withDynSession{
    getUserByToken(token) match {
      case Some(user) => {
        (Q.u + s"INSERT INTO $USER_MARKED_LOG_TABLE_NAME(user_id,link) VALUES (${user.id},"+?link+")").execute
      }
      case _ => {

      }
    }
  }

  def logAuth(token: String,ip: String)(implicit db: Database): Unit = db.withDynSession{
    getUserByToken(token) match {
      case Some(user) => {
        (Q.u + s"INSERT INTO $USER_AUTH_IPS_TABLE_NAME(user_id,ip,timestamp) VALUES(${user.id},"+?ip+",NOW())").execute
      }
      case _ => {

      }
    }
  }

  def logFailedAuth(username: String, password: String,token: String,ip: String)(implicit db: Database): Unit = db.withDynSession{
    (Q.u + s"INSERT INTO $USER_FAILED_AUTH_IPS_TABLE_NAME(username,pwd,token,ip,timestamp) " +
      s"VALUES("+?username+","+?password+","+?token+","+?ip+",NOW())").execute
  }

}
