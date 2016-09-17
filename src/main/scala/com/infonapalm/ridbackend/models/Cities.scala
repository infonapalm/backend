package com.infonapalm.ridbackend.models

import com.infonapalm.ridbackend.vkStructs.CityInfo

import scala.slick.jdbc.{StaticQuery => Q}
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

/**
 * Created with IntelliJ IDEA.
 * User: infonapalm
 * Date: 6/17/15
 * Time: 9:46 AM

 */
case class CityStatistics(name: String, count: String, cid: String)

object Cities {
  val TABLE_NAME = "cities"
  lazy val allCitiesWithNames = getAllCitiesWithNames

  def saveToDB(cities: List[CityInfo]): Unit = {
    cities.foreach(
      x => (Q.u + s"INSERT IGNORE INTO $TABLE_NAME(cid,name) VALUES(" +? x.cid + "," +? x.name + ")" ).execute
    )
  }

  def saveToDB(db: Database,cities: List[CityInfo]): Unit = db.withDynSession { saveToDB(cities) }

  def getAllCitiesWithNames(): Map[String,String] = {
    val q = Q[(String,String)] +
      s"""
         |SELECT cid,name
         |FROM $TABLE_NAME
       """.stripMargin

    q().list.foldLeft(Map[String,String]())((m,r) => {
      m + (r._1 -> r._2)
    })
  }

  def getStatistics(name: String = "")(implicit db: Database): List[CityStatistics] = db.withDynSession{
    val whereName = if(!name.isEmpty){
      s" AND name LIKE '%$name%' "
    }else{
      ""
    }
    val q = Q[(String,String,String)] +
    s"""
       |SELECT cities.name,count(*) cnt,$TABLE_NAME.cid
       |FROM friends
       |LEFT JOIN $TABLE_NAME ON friends.city = $TABLE_NAME.cid
       |WHERE group_id != 0 $whereName
       |GROUP BY $TABLE_NAME.name
       |ORDER BY cnt DESC
       |LIMIT 50
     """.stripMargin
    q().list.map(x => new CityStatistics(x._1,x._2,x._3))
  }

  def getCityById(cid: String): String = {
//    lazy val default = {
//      val q = Q[String,String] + s"SELECT name FROM $TABLE_NAME WHERE cid = ?"
//      q(cid).list.headOption.getOrElse("")
//    }
    allCitiesWithNames.getOrElse(cid,"")
  }

  def getCityById(db: Database,cid: String): String = db.withDynSession { getCityById(cid) }
}
