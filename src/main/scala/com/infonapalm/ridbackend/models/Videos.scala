package com.infonapalm.ridbackend.models

import com.infonapalm.ridbackend.vkStructs.{PhotosInfo, VideoInfo}

import scala.slick.jdbc.{StaticQuery => Q}
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

/**
  * Created with IntelliJ IDEA.
  * User: infonapalm
  * Date: 3/30/16
  * Time: 7:48 PM

  */
object Videos {
  val TABLE_NAME = "videos"
  val FIELDS = "vid,owner_id,image,image_medium,player,date,title"

  implicit def listTupleToVideo(l: List[(String,String,String,String,String,String,String)]): List[VideoInfo] = l.map(x =>
    VideoInfo(x._1,x._2,x._3,x._4,x._5,x._6,x._7)
  )

  def saveToDB(videos: List[VideoInfo])(implicit db: Database): Unit = db.withDynSession{
    if(!videos.isEmpty){
      val values = videos.map(x => {
        "('"+x.vid+"','"+x.owner_id+"','"+x.image+"','" + x.image_medium + "','" + x.player + "'," + x.date.toLong + "," +
          "'" + x.title.replace("\"","").replace("'","") + "',date(from_unixtime(" + x.date.toLong + ")))"
      }).mkString(",")

      try {
        (Q.u +
          s"""
             | INSERT IGNORE INTO $TABLE_NAME($FIELDS,dateStr)
             | VALUES $values
          """.stripMargin).execute
      }catch{
        case x => {
          System.out.println(s"""
                                | INSERT IGNORE INTO $TABLE_NAME($FIELDS,dateStr)
          """.stripMargin)
          System.out.println(x.getMessage);
        }
      }
    }
  }

  def getAllVideosForUser(uid: String)(implicit db: Database): List[VideoInfo] = db.withDynSession{
    val q = Q[String,(String,String,String,String,String,String,String)] +
      s"""
         |SELECT $FIELDS
         |FROM $TABLE_NAME
         |WHERE owner_id = ?
       """.stripMargin

    q(uid).list
  }

  def getRussiansVideosByDate(offsetDate: Int = 0)(implicit db: Database): List[VideoInfo] = db.withDynSession{
    val qDate = Q[(String)] +
      s"""
         |SELECT dateStr
         |FROM $TABLE_NAME
         |WHERE owner_id IN (SELECT uid FROM ${Friends.TABLE_NAME} WHERE group_id = ${Friends.RUSSIAN_GROUP_IDX})
         |AND deleted = FALSE AND player not like '%youtube%'
         |AND dateStr >= '2014-01-01'
         |GROUP BY dateStr
         |ORDER BY dateStr ASC
         |LIMIT 1
         |OFFSET $offsetDate
       """.stripMargin
    val date = qDate().list.headOption.getOrElse("")

    val q = Q[(String,String,String,String,String,String,String)] +
      s"""
         |SELECT $FIELDS
         |FROM $TABLE_NAME
         |WHERE owner_id IN (SELECT uid FROM ${Friends.TABLE_NAME} WHERE group_id = ${Friends.RUSSIAN_GROUP_IDX})
         |AND deleted = FALSE AND player not like '%youtube%'
         |AND dateStr = '$date'
         |ORDER BY date DESC
       """.stripMargin
    q().list
  }

  def getRussiansVideosByDateTotal()(implicit db: Database) : String= db.withDynSession{
    val qDate = Q[String] +
      s"""
         |SELECT COUNT(*) FROM (
         |SELECT dateStr
         |FROM $TABLE_NAME
         |WHERE owner_id IN (SELECT uid FROM ${Friends.TABLE_NAME} WHERE group_id = ${Friends.RUSSIAN_GROUP_IDX})
         |AND deleted = FALSE AND player not like '%youtube%'
         |AND dateStr >= '2014-01-01'
         |GROUP BY dateStr
         |) q1
       """.stripMargin

    qDate.list.headOption.getOrElse("")
  }

  def deleteVideo(vid: String,uid: String)(implicit db: Database): Unit = db.withDynSession{
    (Q.u + s"UPDATE $TABLE_NAME SET deleted = TRUE WHERE vid = " +? vid + " AND owner_id = " +? uid).execute
  }

  def deleteAllVideos(list: List[(String,String)])(implicit db: Database): Unit = db.withDynSession{
    val vids = list.map(_._1).mkString(",")
    val q = Q[(String,String)] +
      s"""
         |SELECT vid,count(*) cnt
         |FROM $TABLE_NAME
         |WHERE vid IN ($vids)
         |GROUP BY vid
         |HAVING cnt > 1;
       """.stripMargin

    val vidsWithMoreThenOneUID = q().list.map(_._1)
    val vidsToDelete = list.map(_._1).diff(vidsWithMoreThenOneUID).mkString(",")
    if(!vidsToDelete.isEmpty){
      (Q.u + s"UPDATE $TABLE_NAME SET deleted = TRUE WHERE vid IN ($vidsToDelete)").execute
    }

    val vidsToDeleteOneByOne = list.map(_._1).intersect(vidsWithMoreThenOneUID)
    list.filter(x => vidsToDeleteOneByOne.contains(x._1)).foreach(x => deleteVideo(x._1,x._2))
  }

}
