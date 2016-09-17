package com.infonapalm.ridbackend.models

import scala.slick.jdbc.{StaticQuery => Q}
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession


/**
 * Created with IntelliJ IDEA.
 * User: infonapalm
 * Date: 8/15/15
 * Time: 1:09 PM

 */
case class TagStatistic(name: String, count: String)

object Tags {
  val TABLE_NAME = "tags"
  val RELATION_TABLE_NAME = "friend_tag"
  val PHOTOS_RELATION_TABLE_NAME = "photos_tag"

  val GOOGLE_VISUAL_TAG_NAME = "GoogleVisualFeatureDetection"
  val NO_PHOTOS_FOR_TAG_NAME = "NoPhotosForGoogleDetection"

  val MILITARY_TAG_LIST = List("transport","vehicle","missle","soldier","machine","army","law enforcement",
    "military","law enforcement","military officer","military person","air force","document","hunting","airsoft",
    "gun","paratrooper","troop")

  def saveTags(uid: String, tags: String, delete: Boolean = true): Unit = {
    // Remove all old tags
    if(delete){
      (Q.u + s"DELETE FROM $RELATION_TABLE_NAME WHERE uid = " +? uid).execute
    }
    // Insert new data
    tags.split(",").filter(!_.isEmpty).foreach(x => {
      (Q.u + s"INSERT IGNORE INTO $TABLE_NAME(name) VALUES(" +? x + ")").execute
      val q = Q[String,String] + s"SELECT tid FROM $TABLE_NAME WHERE name = ?"
      (Q.u + s"INSERT IGNORE INTO $RELATION_TABLE_NAME(uid,tid) VALUES("+?uid+","+?q(x).list.head+")").execute
    })
  }

  def savePhotosTags(pid: String, uid: String, tags: String)(implicit db: Database): Unit = db.withDynSession{
    // Remove all old tags
    (Q.u + s"DELETE FROM $PHOTOS_RELATION_TABLE_NAME WHERE uid = " +? uid + " AND pid = " +? pid).execute
    // Insert new data
    tags.split(",").filter(!_.isEmpty).foreach(x => {
      (Q.u + s"INSERT IGNORE INTO $TABLE_NAME(name) VALUES(" +? x + ")").execute
      val q = Q[String,String] + s"SELECT tid FROM $TABLE_NAME WHERE name = ?"
      val tid = q(x).list.head
      (Q.u + s"INSERT IGNORE INTO $PHOTOS_RELATION_TABLE_NAME(pid,uid,tid) VALUES("+?pid+","+?uid+","+?tid+")").execute
    })
    saveTags(uid,tags,false)
  }

  def appendTag(uid: String, tag: String)(implicit db: Database): Unit = db.withDynSession{
    (Q.u + s"INSERT IGNORE INTO $TABLE_NAME(name) VALUES(" +? tag + ")").execute
    val q = Q[String,String] + s"SELECT tid FROM $TABLE_NAME WHERE name = ?"
    (Q.u + s"INSERT IGNORE INTO $RELATION_TABLE_NAME(uid,tid) VALUES("+?uid+","+?q(tag).list.head+")").execute
  }

  def appendPhotosTag(pid: String, uid: String, tag: String)(implicit db: Database): Unit = db.withDynSession{
    (Q.u + s"INSERT IGNORE INTO $TABLE_NAME(name) VALUES(" +? tag + ")").execute
    val q = Q[String,String] + s"SELECT tid FROM $TABLE_NAME WHERE name = ?"
    val tid = q(tag).list.head
    (Q.u + s"INSERT IGNORE INTO $PHOTOS_RELATION_TABLE_NAME(pid,uid,tid) VALUES("+?pid+","+?uid+","+?tid+")").execute
    appendTag(uid,tag)
  }

  def getPhotosTags(pid: String, uid: String)(implicit db: Database): String = db.withDynSession{
    val q = Q[(String,String),String] +
      s"""
         |SELECT $TABLE_NAME.name
         |FROM $TABLE_NAME
         |INNER JOIN $PHOTOS_RELATION_TABLE_NAME ON $PHOTOS_RELATION_TABLE_NAME.tid = $TABLE_NAME.tid
         |WHERE $PHOTOS_RELATION_TABLE_NAME.pid = ? AND $PHOTOS_RELATION_TABLE_NAME.uid = ?
         |ORDER BY name ASC
       """.stripMargin
    q(pid,uid).list.mkString(",")
  }

  def getAllTags(uid: String): String = {
    val q = Q[String,String] +
      s"""
         |SELECT $TABLE_NAME.name
         |FROM $TABLE_NAME
         |INNER JOIN $RELATION_TABLE_NAME ON $RELATION_TABLE_NAME.tid = $TABLE_NAME.tid
         |WHERE $RELATION_TABLE_NAME.uid = ?
         |ORDER BY name ASC
       """.stripMargin
    q(uid).list.mkString(",")
  }

  def getUIDsByTag(tag: String): List[String] = {
    val q = Q[String,String] +
      s"""
         |SELECT $RELATION_TABLE_NAME.uid
         |FROM $TABLE_NAME
         |INNER JOIN $RELATION_TABLE_NAME on $RELATION_TABLE_NAME.tid = $TABLE_NAME.tid
         |WHERE $TABLE_NAME.name = ?
         |ORDER BY $RELATION_TABLE_NAME.timestamp DESC
       """.stripMargin
    q(tag).list
  }

  def getTagsStatistics(name: String = "")(implicit db: Database): List[TagStatistic] = db.withDynSession{
    val (whereStmt,havingStmt) = if(!name.isEmpty){
      (s" WHERE name like '%$name%' "," ")
    }else{
      (s" WHERE 1=1 "," HAVING cnt > 2 ")
    }
    val q = Q[(String, String)] +
      s"""
         |SELECT name,COUNT(*) cnt
         |FROM $RELATION_TABLE_NAME
         |INNER JOIN $TABLE_NAME on $TABLE_NAME.tid = $RELATION_TABLE_NAME.tid
         |INNER JOIN ${Friends.TABLE_NAME} on ${Friends.TABLE_NAME}.uid = $RELATION_TABLE_NAME.uid AND ${Friends.TABLE_NAME}.is_deleted = FALSE
         |$whereStmt
         |GROUP BY name
         |$havingStmt
         |ORDER BY cnt DESC
       """.stripMargin
    q().list.map(x => new TagStatistic(x._1,x._2))
  }

  def getTagsForAutocomplete(str: String)(implicit db: Database): List[(String,String)] = db.withDynSession{
    if(str.isEmpty) return List()
    val q = Q[(String,String)] +
      s"""
         |SELECT tid,name
         |FROM $TABLE_NAME
         |WHERE name like '%$str%'
       """.stripMargin
    q().list
  }
}
