package com.infonapalm.ridbackend.models

import com.infonapalm.ridbackend.vkStructs.{CountryInfo, CityInfo}

import scala.slick.jdbc.{StaticQuery => Q}
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

/**
 * Created with IntelliJ IDEA.
 * User: infonapalm
 * Date: 6/17/15
 * Time: 9:47 AM

 */
object Countries {
  val TABLE_NAME = "countries"
  lazy val allCountriesWithNames = getAllCountriesWithNames

  def saveToDB(countries: List[CountryInfo]): Unit = {
    countries.foreach(
      x => (Q.u + s"INSERT IGNORE INTO $TABLE_NAME(cid,name) VALUES(" +? x.cid + "," +? x.name + ")" ).execute
    )
  }

  def saveToDB(db: Database,countries: List[CountryInfo]): Unit = db.withDynSession { saveToDB(countries) }

  def getAllCountriesWithNames(): Map[String,String] = {
    val q = Q[(String,String)] +
      s"""
         |SELECT cid,name
         |FROM $TABLE_NAME
       """.stripMargin

    q().list.foldLeft(Map[String,String]())((m,r) => {
      m + (r._1 -> r._2)
    })
  }

  def getCountryById(cid: String): String = {
//    lazy val default = {
//      val q = Q[Int,String] + s"SELECT name FROM $TABLE_NAME WHERE cid = ?"
//      q(cid.toInt).list.headOption.getOrElse("")
//    }
    allCountriesWithNames.getOrElse(cid,"")
  }

  def getCountryById(db: Database,cid: String): String = db.withDynSession { getCountryById(cid) }
}
