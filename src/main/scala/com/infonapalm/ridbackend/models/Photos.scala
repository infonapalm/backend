package com.infonapalm.ridbackend.models

import java.text.SimpleDateFormat

import com.infonapalm.ridbackend.vkStructs.{FriendInfo, PhotosInfo}

import scala.slick.jdbc.{StaticQuery => Q}
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import com.infonapalm.ridbackend.Utils.StringUtils._
import com.twitter.inject.Logging

import scala.util.Try

/**
 * Created with IntelliJ IDEA.
 * User: infonapalm
 * Date: 4/29/15
 * Time: 11:33 AM
  *
  */
//TODO: Add lat,lon if they are in API response
case class PhotoMap(lat: String,lng: String,src: String,src_big: String,photo: String,uid: String,
                    src_xbig: String = "",src_xxbig: String = "",src_xxxbig: String = "")

object Photos extends Logging {
  val TABLE_NAME = "photos"
  val TABLE_NAME_OLD = "photos_old"
  val TABLE_NAME_POLYGON_MAP = "photos_polygon_map"
  val FIELDS = "pid,owner_id,src,src_big,created,lat,lng,src_xbig,src_xxbig,src_xxxbig"
  val FIELDS_OLD = "pid,owner_id,src,src_big,created,lat,lng"
  val POLYGON_LIMIT = 100

  val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  implicit def tuple10ToPhotoInfo(l: Option[(String,String,String,String,String,String,String,String,String,String)]): Option[PhotosInfo] = l.map(x =>
    PhotosInfo(x._1,x._2,x._3,x._4,x._5,x._6,x._7,x._8,x._9,x._10)
  )
  implicit def listTupleToPhotoMapLatLng(l: List[(String,String,String,String,String,String)]): List[PhotoMap] = l.map(x =>
    PhotoMap(x._1,x._2,x._3,x._4,x._5,x._6)
  )
  implicit def listTuple10ToPhotoInfo(l: List[(String,String,String,String,String,String,String,String,String,String)]): List[PhotosInfo] = l.map(x =>
    PhotosInfo(x._1,x._2,x._3,x._4,x._5,x._6,x._7,x._8,x._9,x._10)
  )
  implicit def listTuple11ToPhotoInfo(l: List[(String,String,String,String,String,String,String,String,String,String,String)]): List[PhotosInfo] = l.map(x =>
    PhotosInfo(x._1,x._2,x._3,x._4,x._5,x._6,x._7,x._8,x._9,x._10,x._11)
  )
  implicit def listTuple12ToPhotoInfo(l: List[(String,String,String,String,String,String,String,String,String,String,String,String)]): List[PhotosInfo] = l.map(x =>
    PhotosInfo(x._1,x._2,x._3,x._4,x._5,x._6,x._7,x._8,x._9,x._10,x._11,tags_count = x._12)
  )

  def saveToDB(photo: PhotosInfo,tbl: String = TABLE_NAME)(implicit db: Database): Unit = db.withDynSession{
    Try((Q.u + s"INSERT IGNORE INTO $tbl($FIELDS,dateStr) VALUES (" +? photo.pid.toDoubleOrZero + "," +? photo.owner_id.toDoubleOrZero +
      "," +? photo.src + "," +? photo.src_big + "," +? photo.created.toDoubleOrZero + "," +? photo.lat +
      "," +? photo.long + "," +? photo.src_xbig + "," +? photo.src_xxbig + "," +? photo.src_xxxbig + ",date(from_unixtime("+?photo.created.toLong+")) ) ").execute)
  }

  def saveAllToDB(photos: List[PhotosInfo],tbl: String = TABLE_NAME)(implicit db: Database): Unit = db.withDynSession{
    if(!photos.isEmpty){
      val values = photos.map(x => {
        "('"+x.pid+"','"+x.owner_id+"','"+x.src+"','" + x.src_big + "','"+x.created+"','"+x.lat+"','"+x.long+"'," +
          "'"+x.src_xbig+"','"+x.src_xxbig+"','"+x.src_xxxbig+"',date(from_unixtime("+x.created+")))"
      }).mkString(",")

      try {
        (Q.u +
          s"""
             | INSERT IGNORE INTO $tbl($FIELDS,dateStr)
             | VALUES $values
        """.stripMargin).execute
      }catch{
        case x => {
          System.out.println(s"""
            | INSERT IGNORE INTO $tbl($FIELDS,dateStr)
          """.stripMargin)
          System.out.println(x.getMessage);
        }
      }
      println("Inserted Photos:  " + photos.size)
    }
  }

  def updatePhotosLikes(photos: List[PhotosInfo])(implicit db: Database): Unit = db.withDynSession{
    if(!photos.isEmpty){
      val values = photos.map(photo => {
        "('" + photo.pid + "','" + photo.owner_id + "','" + photo.likes.count + "','" +photo.src+ "','" +photo.created+"')"
      }).mkString(",")

      (Q.u + s"INSERT INTO $TABLE_NAME(pid,owner_id,likes,src,created) VALUES $values ON DUPLICATE KEY UPDATE likes = VALUES(likes)").execute
    }

  }

  def getPhotoByUserID(uid: String)(implicit db: Database): List[PhotosInfo] = db.withDynSession{
    val q = Q[String,(String,String,String,String,String,String,String,String,String,String,String)] +
      s"""
        |SELECT $FIELDS,
        | (
        |   SELECT COUNT(*) FROM ${Tags.PHOTOS_RELATION_TABLE_NAME}
        |   WHERE ${Tags.PHOTOS_RELATION_TABLE_NAME}.uid = $TABLE_NAME.owner_id AND ${Tags.PHOTOS_RELATION_TABLE_NAME}.pid = $TABLE_NAME.pid
        | ) count
        |FROM $TABLE_NAME
        |WHERE owner_id = ?
        |ORDER BY created DESC
      """.stripMargin
    val photos = q(uid).list.map(x => new PhotosInfo(x._1,x._2,x._3,x._4,x._5,x._6,x._7,x._8,x._9,x._10,tags_count = x._11))
    val qOld = Q[String,(String,String,String,String,String,String,String,String)] +
      s"""
          |SELECT $FIELDS_OLD,
          | (
          |   SELECT COUNT(*) FROM ${Tags.PHOTOS_RELATION_TABLE_NAME}
          |   WHERE ${Tags.PHOTOS_RELATION_TABLE_NAME}.uid = $TABLE_NAME_OLD.owner_id AND ${Tags.PHOTOS_RELATION_TABLE_NAME}.pid = $TABLE_NAME_OLD.pid
          | ) count
          |FROM $TABLE_NAME_OLD
          |WHERE owner_id = ?
          |ORDER BY created DESC
      """.stripMargin
    val photosOld = qOld(uid).list.map(x => new PhotosInfo(x._1,x._2,x._3,x._4,x._5,x._6,x._7,tags_count = x._8))
    val qPolygonMap = Q[String,(String,String,String,String,String,String,String,String)] +
      s"""
          |SELECT $FIELDS_OLD,
          | (
          |   SELECT COUNT(*) FROM ${Tags.PHOTOS_RELATION_TABLE_NAME}
          |   WHERE ${Tags.PHOTOS_RELATION_TABLE_NAME}.uid = $TABLE_NAME_POLYGON_MAP.owner_id AND ${Tags.PHOTOS_RELATION_TABLE_NAME}.pid = $TABLE_NAME_POLYGON_MAP.pid
          | ) count
          |FROM $TABLE_NAME_POLYGON_MAP
          |WHERE owner_id = ?
          |ORDER BY created DESC
      """.stripMargin
    val photosPolygonMap = qPolygonMap(uid).list.map(x => new PhotosInfo(x._1,x._2,x._3,x._4,x._5,x._6,x._7,tags_count = x._8))
    (photos++photosOld++photosPolygonMap).groupBy(_.src).map(_._2.head).toList
  }

  def getLatLngForUsers(users: List[FriendInfo])(implicit db: Database): List[PhotoMap] = db.withDynSession{
    if(users.isEmpty) return List()
    val uids = users.map(_.uid).mkString(",")
    val q = Q[(String,String,String,String,String,String)] +
    s"""
       |SELECT lat,lng,src,src_big,photo,uid
       |FROM $TABLE_NAME
       |INNER JOIN friends ON uid = owner_id
       |WHERE lat != 0 AND lng != 0 AND owner_id IN ($uids)
     """.stripMargin

    val time = System.currentTimeMillis()
    val res = q().list
    System.out.println(" getLatLngForUsers Timing:  " + (System.currentTimeMillis() - time))
    res
  }

  def getRussiansPhotosByDate(offsetDate: Int = 0)(implicit db: Database): List[PhotosInfo] = db.withDynSession{
    val qDate = Q[(String)] +
      s"""
         |SELECT dateStr
         |FROM photos
         |  WHERE dateStr >= '2014-01-01'
         |GROUP BY dateStr
         |ORDER BY dateStr ASC
         |LIMIT 1
         |OFFSET $offsetDate
       """.stripMargin
    val date = qDate().list.headOption.getOrElse("")

    val q = Q[(String,String,String,String,String,String,String,String,String,String)] +
      s"""
         |SELECT $FIELDS
         |FROM ${Friends.TABLE_NAME}
         |INNER JOIN $TABLE_NAME ON owner_id = friends.uid AND dateStr = '$date'
         |WHERE group_id = ${Friends.RUSSIAN_GROUP_IDX}
         |AND deleted = 0
         |ORDER BY created DESC
       """.stripMargin
    q().list
  }

  def getRussiansPhotosByDateTotal()(implicit db: Database) : String= db.withDynSession{
    val qDate = Q[String] +
      s"""
         |SELECT COUNT(*) FROM (
         |SELECT dateStr
         |FROM photos
         |  WHERE dateStr >= '2014-01-01'
         |GROUP BY dateStr
         |) q1
       """.stripMargin

    qDate.list.headOption.getOrElse("")
  }

  def getRussainsPhotosByTag(tag: String, offsetDate: Int = 0)(implicit db: Database): List[PhotosInfo] = db.withDynSession{
    val qDate = Q[String,String] +
      s"""
         |SELECT dateStr
         |FROM photos
         |  WHERE dateStr >= '2014-01-01'
         |  AND owner_id IN (select uid from friend_tag where tid in (select tid from tags where name like ?))
         |GROUP BY dateStr
         |ORDER BY dateStr ASC
         |LIMIT 1
         |OFFSET $offsetDate
       """.stripMargin
    val date = qDate(s"%$tag%").list.headOption.getOrElse("")

    val q = Q[String,(String,String,String,String,String,String,String,String,String,String)] +
      s"""
         |SELECT $FIELDS
         |FROM $TABLE_NAME
         |WHERE owner_id IN (select uid from friend_tag where tid in (select tid from tags where name like ?))
         |AND dateStr = '$date'
         |AND deleted = 0
         |ORDER BY created DESC
       """.stripMargin
    q(s"%$tag%").list
  }

  def getRussiansPhotosByTagTotal(tag: String)(implicit db: Database) : String= db.withDynSession{
    val qDate = Q[String,String] +
      s"""
         |SELECT COUNT(*) FROM (
         |SELECT dateStr
         |FROM photos
         |  WHERE dateStr >= '2014-01-01' AND owner_id IN (select uid from friend_tag where tid in (select tid from tags where name like ?))
         |GROUP BY dateStr
         |) q1
       """.stripMargin

    qDate(tag).list.headOption.getOrElse("")
  }

  def getPhotosByPolygon(limit: Int = POLYGON_LIMIT,offsetDate: Int = 0,offsetPhotos: Int = 0)(implicit db: Database): List[PhotosInfo] = db.withDynSession{
    val date = getPolygonDates()(db)(offsetDate)
    val newOffset = offsetPhotos*limit
    val q = Q[String,(String,String,String,String,String,String,String,String,String,String,String,String)] +
      s"""
         |SELECT $FIELDS,group_id,
         | (
         |   SELECT COUNT(*) FROM ${Tags.PHOTOS_RELATION_TABLE_NAME}
         |   WHERE ${Tags.PHOTOS_RELATION_TABLE_NAME}.uid = $TABLE_NAME_POLYGON_MAP.owner_id AND ${Tags.PHOTOS_RELATION_TABLE_NAME}.pid = $TABLE_NAME_POLYGON_MAP.pid
         | ) count
         |FROM $TABLE_NAME_POLYGON_MAP
         |LEFT JOIN ${Friends.TABLE_NAME} on ${Friends.TABLE_NAME}.uid = $TABLE_NAME_POLYGON_MAP.owner_id
         |WHERE dateStr = ?
         |AND deleted = 0
         |ORDER BY created ASC
         |LIMIT $limit OFFSET $newOffset
       """.stripMargin

    q(date).list
  }

  def getPolygonDates()(implicit db: Database): List[String] = db.withDynSession{
    val qDate = Q[String] +
      s"""
         |SELECT dateStr AS dt
         |FROM $TABLE_NAME_POLYGON_MAP
         |GROUP BY dt
         |ORDER BY dt asc
     """.stripMargin
    qDate().list
  }

  def getPolygonDatesWithCounts()(implicit db: Database): List[(String,String)] = db.withDynSession{
    val qDate = Q[(String,String)] +
      s"""
         |SELECT dateStr AS dt, count(*)
         |FROM $TABLE_NAME_POLYGON_MAP
         |WHERE deleted = 0
         |GROUP BY dt
         |ORDER BY dt ASC
       """.stripMargin

    qDate().list
  }

  def getPolygonByDateTotal(offsetDate: Int = 0)(implicit db: Database): String = db.withDynSession{
    val date = getPolygonDates()(db)(offsetDate)
    val (from,to) = dateToTimestampRange(date)
    val q = Q[String] +
      s"""
         |SELECT count(*)
         |FROM $TABLE_NAME_POLYGON_MAP
         |LEFT JOIN ${Friends.TABLE_NAME} on ${Friends.TABLE_NAME}.uid = $TABLE_NAME_POLYGON_MAP.owner_id
         |WHERE created >= $from AND created <= $to
         |AND deleted = 0
       """.stripMargin
    q().list.head
  }

  def getPolygonTotal()(implicit db: Database): String = db.withDynSession{
    getPolygonDates()(db).size.toString
  }

  def deletePhoto(pid: String, uid: String,tbl: String = TABLE_NAME)(implicit db: Database) = db.withDynSession{
    (Q.u + s"UPDATE $tbl SET deleted = 1 WHERE pid = " +? pid + " AND owner_id = " +? uid).execute
  }

  def deleteAllPhotos(list: List[(String,String)], tbl: String = TABLE_NAME)(implicit db: Database): Unit = db.withDynSession{
    //TODO: Find is there are photos with same pid but different users
    val pids = list.map(_._1).mkString(",")
    val q = Q[(String,String)] +
      s"""
         |SELECT pid,count(*) cnt
         |FROM $TABLE_NAME_POLYGON_MAP
         |WHERE pid IN ($pids)
         |GROUP BY pid
         |HAVING cnt > 1;
       """.stripMargin

    val pidsWithMoreThenOneUID = q().list.map(_._1)
    val pidsToDelete = list.map(_._1).diff(pidsWithMoreThenOneUID).mkString(",")
    (Q.u + s"UPDATE $tbl SET deleted = 1 WHERE pid IN ($pidsToDelete)").execute

    val pidsToDeleteOneByOne = list.map(_._1).intersect(pidsWithMoreThenOneUID)
    list.filter(x => pidsToDeleteOneByOne.contains(x._1)).foreach(x => deletePhoto(x._1,x._2,tbl))
  }

  def deleteAllPhotos(list: List[(String,String)])(implicit db: Database) = db.withDynSession{
    val pids = list.map(_._1)
  }

  def search(srcXBig: String)(implicit db: Database): Option[PhotosInfo] = db.withDynSession {
    val q = Q[(String,String),(String,String,String,String,String,String,String,String,String,String)] +
      s"""
         |SELECT $FIELDS
         |FROM $TABLE_NAME
         |WHERE src_xbig = ? OR src_big = ?
       """.stripMargin
    val tblResult = q(srcXBig,srcXBig).list.headOption

    if(tblResult.isEmpty){
      val qPolygon = Q[(String,String),(String,String,String,String,String,String,String,String,String,String)] +
        s"""
           |SELECT $FIELDS
           |FROM $TABLE_NAME_POLYGON_MAP
           |WHERE src_xbig = ? OR src_big = ?
       """.stripMargin

      qPolygon(srcXBig,srcXBig).list.headOption
    }else{
      tblResult
    }
  }

  def getPhotoForVisualRecognition(userID: String)(implicit db: Database): (String,String,String) = db.withDynSession{
    val q = Q[String, (String,String,String)] +
      s"""
         |  SELECT pid,owner_id,src_xbig
         |  FROM $TABLE_NAME
         |  WHERE owner_id = ? AND likes > 5 AND photos.created > 1393632000 AND src_xbig != ''
         |  ORDER BY created DESC
         |  LIMIT 1
       """.stripMargin

    q(userID).list.headOption.getOrElse(("",userID,""))
  }

  def updateDateStringInPhotos()(implicit db: Database): Unit = db.withDynSession{
    (Q.u + s"update $TABLE_NAME set dateStr = date(from_unixtime(created)) WHERE dateStr is NULL").execute
  }

  def getAllPotentialPhotosWithGeolocation()(implicit db: Database): List[PhotosInfo] = db.withDynSession{
    val qPhotos = Q[(String,String,String,String,String,String,String,String,String,String)] +
      s"""
         |SELECT $FIELDS
         |FROM $TABLE_NAME
         |  INNER JOIN ${Friends.TABLE_NAME} ON owner_id = uid AND friends.is_potential > 0 AND group_id = 0 AND is_deleted = FALSE
         |WHERE lat != 0 AND created > 1388534400
       """.stripMargin
    qPhotos().list
  }

  def dateToTimestampRange(date: String): (Long,Long) = {
    (
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s"$date 00:00:00").getTime/1000L,
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s"$date 23:59:59").getTime/1000L
    )
  }
}
