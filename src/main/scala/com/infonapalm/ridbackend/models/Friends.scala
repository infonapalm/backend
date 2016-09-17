package com.infonapalm.ridbackend.models

import java.sql.SQLException

import com.infonapalm.ridbackend.TemplateStructs.{FriendsPairTemplate}
import com.infonapalm.ridbackend.vkStructs.FriendInfo
import org. postgresql.util.PSQLException

import scala.slick.jdbc.{StaticQuery => Q}
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

import com.infonapalm.ridbackend.Utils.StringUtils._

/**
 * Created with IntelliJ IDEA.
 * User: infonapalm
 * Date: 5/1/15
 * Time: 12:53 PM
  *
  */
object Friends {
  val TABLE_NAME = "friends"
  val RELATION_TABLE_NAME = "friends_relation"
  val TABLE_NAME_POTENTIAL_WITH_PHOTOS_IN_POLYGON = "potential_with_photos_in_polygon"
  val FIELDS = s"$TABLE_NAME.uid,$TABLE_NAME.first_name,$TABLE_NAME.last_name,$TABLE_NAME.domain,$TABLE_NAME.photo," +
    s"$TABLE_NAME.city,$TABLE_NAME.country,$TABLE_NAME.sex,$TABLE_NAME.group_id,$TABLE_NAME.friends_col,$TABLE_NAME.marked"
  
  val MAIN_GROUP_IDX = "0"
  val RUSSIAN_GROUP_IDX = "1"

  implicit def listTuple2FriendsInfo(list: List[(String,String,String,String,String,String,String,String,String,String,String)]): List[FriendInfo] =
    list.map(x => new FriendInfo(uid = x._1,first_name = x._2,last_name = x._3, domain = x._4, photo = x._5,
    city = x._6.toInt,country = x._7.toInt, sex = x._8.toInt, group_id = x._9, user_id = x._1,marked = x._11))

  implicit def OptionTuple2FriendsInfo(entry: Option[(String,String,String,String,String,String,String,String,String,String,String)]): Option[FriendInfo] =
    entry match {
      case Some(x) => Some(
        new FriendInfo(uid = x._1,first_name = x._2,last_name = x._3, domain = x._4, photo = x._5,
          city = x._6.toInt, country = x._7.toInt, sex = x._8.toInt, group_id = x._9, user_id = x._1,marked = x._11)
      )
      case None => None
    }

  implicit def OptionTuple2FriendsInfoWithComments(
    entry: Option[(String,String,String,String,String,String,String,String,String,String,String,String)]
  ): Option[FriendInfo] =
    entry match {
      case Some(x) => Some(
        new FriendInfo(uid = x._1,first_name = x._2,last_name = x._3, domain = x._4, photo = x._5,
          city = x._6.toInt, country = x._7.toInt, sex = x._8.toInt, group_id = x._9, user_id = x._1,marked = x._11,
          comments = x._12)
      )
      case None => None
    }

  def saveToDB(friend: FriendInfo)(implicit db: Database): Unit = db.withDynSession{
    //TODO: Add versioning if profile will change
    val uid = friend.uid.toDoubleOrZero
    try {
      (Q.u +
        s"INSERT IGNORE INTO $TABLE_NAME($FIELDS,comments) " +
        "VALUES (" +? uid + "," +? friend.first_name + "," +? friend.last_name +
        "," +? friend.domain + "," +? friend.photo + "," +? friend.city + "," +? friend.country + "," +? friend.sex +
        "," +? friend.group_id + "," +? friend.counters.friends + ",null," +? friend.comments +")").execute
    }catch{
      case x => System.out.println(x.getMessage)
    }

    //Update comment (add with newline char, if there was something)
    if(!friend.comments.isEmpty){
      val q = Q[String,String] +
        s"""
           |SELECT comments
           |FROM $TABLE_NAME
           |WHERE uid = ?
         """.stripMargin
      val commentOld = q(friend.uid).list.headOption
      commentOld match {
        case Some(x) => {
          (Q.u + s"UPDATE $TABLE_NAME SET comments = " +? friend.comments + " WHERE uid = " + friend.uid).execute
        }
        case _ =>
      }
    }

    try{
      (Q.u + s"INSERT IGNORE INTO $RELATION_TABLE_NAME(user_id,friend_id) VALUES (" +? friend.request_from.toDoubleOrZero +
        "," +? friend.uid.toDoubleOrZero + ") ").execute
    }catch{
      case x => System.out.println(x.getMessage)
    }
    //Add military as tags
    friend.military.foreach(x => {
      Tags.appendTag(friend.uid,"в/ч" + x.unit)(db)
      Tags.appendTag(friend.uid,"в/ч" + x.unit + s"(${x.from}-${x.until})")(db)
    })
  }

  def deleteUser(userID: String)(implicit db: Database): Unit = db.withDynSession{
    (Q.u + s"UPDATE $TABLE_NAME SET is_deleted = TRUE WHERE uid = " +? userID.toInt).execute
  }
  
  def getFriendsByUserID(uid: String)(implicit db: Database): List[FriendInfo] = db.withDynSession{
    val q = Q[String,(String,String,String,String,String,String,String,String,String,String,String)] +
      s"""
         |SELECT $FIELDS
         |FROM $RELATION_TABLE_NAME rt
         |INNER JOIN $TABLE_NAME t ON t.uid = rt.user_id
         |WHERE user_id = ? AND is_deleted = FALSE
       """.stripMargin
    
    q(uid).list.map(x => new FriendInfo(uid = x._1,first_name = x._2,last_name = x._3, domain = x._4, photo = x._5,
      city = x._6.toInt,sex = x._7.toInt,request_from = uid, group_id = x._8, user_id = x._1))
  }

  def getAllUsers()(implicit db:Database): List[FriendInfo] = db.withDynSession{
    val q = Q[(String,String,String,String,String,String,String,String,String,String,String)] +
      s"""
          |SELECT $FIELDS
          |FROM $TABLE_NAME
          |WHERE is_deleted = FALSE
       """.stripMargin

    q().list
  }

  def getUserByID(uid: String)(implicit db:Database): Option[FriendInfo] = db.withDynSession{
    val q = Q[String,(String,String,String,String,String,String,String,String,String,String,String,String)] +
      s"""
          |SELECT $FIELDS,comments
          |FROM $TABLE_NAME
          |WHERE uid = ?
       """.stripMargin

    q(uid).list.headOption
  }

  def getUserBySearch(str: String)(implicit db:Database): List[FriendInfo] = db.withDynSession{
    val q = Q[(String,String),(String,String,String,String,String,String,String,String,String,String,String)] +
    s"""
       |SELECT $FIELDS
       |FROM $TABLE_NAME
       |WHERE first_name LIKE ? OR last_name LIKE ?
       |ORDER BY last_name ASC, first_name ASC
     """.stripMargin
    q(str,str).list
  }

  def getUserByDomain(domain: String)(implicit db:Database): Option[FriendInfo] = db.withDynSession{
    val q = Q[String,(String,String,String,String,String,String,String,String,String,String,String)] +
      s"""
          |SELECT $FIELDS
          |FROM $TABLE_NAME
          |WHERE domain = ?
       """.stripMargin

    q(domain).list.headOption
  }

  def getAllConnections()(implicit db:Database): List[FriendsPairTemplate] = db.withDynSession{
    val q = Q[(String,String)] + 
      s"""
          |SELECT user_id,friend_id
          |FROM $RELATION_TABLE_NAME
          |WHERE EXISTS( SELECT uid FROM friends WHERE friends.uid = friends_relation.user_id )
          |AND EXISTS( SELECT uid FROM friends WHERE friends.uid = friends_relation.friend_id )
       """.stripMargin
    q().list.map(x => new FriendsPairTemplate(source = x._1.toInt,target = x._2.toInt))
  }

  def getAllConnectionForRussians()(implicit db: Database): List[FriendsPairTemplate] = db.withDynSession{
    val q = Q[(String,String)] +
      s"""
         |SELECT user_id,friend_id
         |FROM $RELATION_TABLE_NAME
         |WHERE user_id IN (SELECT uid FROM friends WHERE group_id = 1) AND
         |friend_id IN (SELECT uid FROM friends WHERE group_id = 1) AND user_id != friend_id
       """.stripMargin
    q().list.map(x => new FriendsPairTemplate(source = x._1.toInt,target = x._2.toInt))
  }

  def getPotential(uid: String)(implicit db:Database): List[FriendInfo] = db.withDynSession {
    val q = Q[(String,String,String,String),(String,String,String,String,String,String,String,String,String,String,String)] +
      s"""
         |SELECT $FIELDS
         |FROM $RELATION_TABLE_NAME
         |INNER JOIN $TABLE_NAME ON $TABLE_NAME.uid = $RELATION_TABLE_NAME.friend_id
         |WHERE $RELATION_TABLE_NAME.user_id = ? AND $RELATION_TABLE_NAME.friend_id != ? AND is_potential > 0 AND group_id != $RUSSIAN_GROUP_IDX AND is_deleted = FALSE
         |UNION ALL
         |SELECT $FIELDS
         |FROM $RELATION_TABLE_NAME
         |INNER JOIN $TABLE_NAME ON $TABLE_NAME.uid = $RELATION_TABLE_NAME.user_id
         |WHERE $RELATION_TABLE_NAME.friend_id = ? AND $RELATION_TABLE_NAME.user_id != ? AND is_potential > 0 AND group_id != $RUSSIAN_GROUP_IDX AND is_deleted = FALSE
       """.stripMargin

    q(uid,uid,uid,uid).list.toSet.toList
  }

  def getPotential(limit: Integer = 100)(implicit db:Database): List[FriendInfo] = db.withDynSession{
    val q = Q[(String,String,String,String,String,String,String,String,String,String,String,String)] +
      s"""
         |SELECT $FIELDS,group_concat(${Tags.TABLE_NAME}.name, '')
         |FROM $TABLE_NAME
         |  left join ${Tags.RELATION_TABLE_NAME} on ${Tags.RELATION_TABLE_NAME}.uid = $TABLE_NAME.uid
         |  left join ${Tags.TABLE_NAME} on ${Tags.TABLE_NAME}.tid = ${Tags.RELATION_TABLE_NAME}.tid
         |WHERE is_potential > 0 AND is_deleted = FALSE AND group_id != 1
         |  group by $TABLE_NAME.uid
         |ORDER BY is_potential DESC
       """.stripMargin

    val time = System.currentTimeMillis()
    val res = q().list.map(x => (new FriendInfo(uid = x._1,first_name = x._2,last_name = x._3, domain = x._4, photo = x._5,
      city = x._6.toInt,sex = x._7.toInt, group_id = x._8, user_id = x._1),x._12))

    val resultWithMilitaryTags = res.filter(x => {
      if(x._2 != null){
        x._2.split(",").toList.exists(tag => {
          Tags.MILITARY_TAG_LIST.contains(tag)
        })
      }else{
        false
      }
    }).map(_._1)

    val result = resultWithMilitaryTags ::: res.filter(x => !resultWithMilitaryTags.map(_.uid).contains(x._1.uid)).map(_._1)

//    //Get peoples with bigger priority
//    val qWithPhotosInPolygon = Q[String] +
//      s"""
//         |SELECT uid
//         |FROM $TABLE_NAME_POTENTIAL_WITH_PHOTOS_IN_POLYGON
//       """.stripMargin
//    val usersWithPhotosInPolygon = qWithPhotosInPolygon().list
//
//    //Sort always work with in reverse
//    val sortedResult = res.sortBy(x => !usersWithPhotosInPolygon.contains(x.uid))
//    sortedResult.take(50)
    System.out.println(" getPotential Timing:  " + (System.currentTimeMillis() - time))
    result.take(100)
  }

  //TODO: Not in use for now
  def setUserProcessedByGoogleVisual(userID: String)(implicit db: Database): Unit = db.withDynSession{
    val q = (Q.u + s"UPDATE $TABLE_NAME SET is_checked_by_google = 1 WHERE uid = " +? userID).execute
  }

  def getPotentialForGoogleVisual(limit: String = "50")(implicit db: Database): List[FriendInfo] = db.withDynSession{
    val q = Q[(String,String,String,String,String,String,String,String,String,String,String)] +
      s"""
         |SELECT $FIELDS FROM $TABLE_NAME WHERE is_potential > 0 AND is_deleted = FALSE AND group_id != 1 AND uid NOT IN (
         |  SELECT $TABLE_NAME.uid
         |  FROM $TABLE_NAME
         |    INNER JOIN ${Tags.RELATION_TABLE_NAME} ON ${Tags.RELATION_TABLE_NAME}.uid = $TABLE_NAME.uid
         |    AND ${Tags.RELATION_TABLE_NAME}.tid = 272766
         |)
         |ORDER BY is_potential DESC
         |LIMIT $limit
       """.stripMargin

    val time = System.currentTimeMillis()
    val res = q().list.map(x => new FriendInfo(uid = x._1,first_name = x._2,last_name = x._3, domain = x._4, photo = x._5,
      city = x._6.toInt,sex = x._7.toInt, group_id = x._8, user_id = x._1))
    System.out.println(" getPotentialForGoogleVisual Timing:  " + (System.currentTimeMillis() - time))
    res
  }

  def getAllRussian()(implicit db:Database): List[FriendInfo] = db.withDynSession{
    val q = Q[(String,String,String,String,String,String,String,String,String,String,String,String,String)] +
      s"""
          |SELECT $FIELDS,x,y
          |FROM $TABLE_NAME
          |WHERE is_deleted = FALSE AND group_id = '$RUSSIAN_GROUP_IDX'
          |ORDER BY marked DESC
       """.stripMargin

    q().list.map(x => new FriendInfo(uid = x._1,first_name = x._2,last_name = x._3, domain = x._4, photo = x._5,
      city = x._6.toInt,country = x._7.toInt, sex = x._8.toInt, group_id = x._9, user_id = x._1,marked = x._11,
      x = x._12, y = x._13))
  }

  def setGroup(uid: String,group: String)(implicit db:Database): Unit = db.withDynSession{
    (Q.u +
      s"""
         |UPDATE $TABLE_NAME
         |SET group_id = '$group', marked = NOW()
         |WHERE uid = $uid
       """.stripMargin).execute
  }

  def userWasParsed(uid: String)(implicit db:Database): Boolean = db.withDynSession{
    val q = Q[String,String] +
      s"""
         |SELECT COUNT(*)
         |FROM $RELATION_TABLE_NAME
         |WHERE user_id = ?
       """.stripMargin
    q(uid).list.head.toInt > 0
  }

  def getCities()(implicit db:Database): List[String] = db.withDynSession{
    val q = Q[String] + s"""SELECT city FROM $TABLE_NAME WHERE city not in (select cid FROM cities) GROUP BY city"""
    q().list
  }

  def getCountries()(implicit db:Database): List[String] = db.withDynSession{
    val q = Q[String] + s"""SELECT country FROM $TABLE_NAME WHERE country not in (select cid FROM countries) GROUP BY country"""
    q().list
  }

  def getRussianFriends(uid: String,russiansOnly: Boolean = true)(implicit db:Database): List[FriendInfo] = db.withDynSession{
    val isRussianStmt = if(russiansOnly){
      s" AND group_id = $RUSSIAN_GROUP_IDX"
    }else{
      ""
    }

    val q = Q[(String,String,String,String),(String,String,String,String,String,String,String,String,String,String,String)] +
    s"""
       |SELECT $FIELDS FROM $RELATION_TABLE_NAME
       |  INNER JOIN $TABLE_NAME ON $TABLE_NAME.uid = $RELATION_TABLE_NAME.friend_id
       |WHERE user_id = ? AND friend_id != ? $isRussianStmt AND is_deleted = FALSE
       |UNION ALL
       |SELECT $FIELDS FROM $RELATION_TABLE_NAME
       |  INNER JOIN $TABLE_NAME ON $TABLE_NAME.uid = $RELATION_TABLE_NAME.user_id
       |WHERE friend_id = ? AND user_id != ? $isRussianStmt AND is_deleted = FALSE
     """.stripMargin

    val time = System.currentTimeMillis()
    val res = q(uid,uid,uid,uid).list.toSet.toList
    System.out.println(" getRussianFriends Timing:  " + (System.currentTimeMillis() - time))
    res
  }

  def getRussianFriends2ndRound(uid: String)(implicit db:Database): List[FriendInfo] = db.withDynSession{
//    val friends1stLevelStr = getRussianFriends(uid,false).map(_.uid).mkString(",")
    val friends1stLevelStr = getRussianFriends(uid).map(_.uid).mkString(",")

    val q = Q[(String,String,String,String,String,String,String,String,String,String,String)] +
    s"""
       |SELECT $FIELDS
       |FROM $RELATION_TABLE_NAME
       |  INNER JOIN $TABLE_NAME ON $TABLE_NAME.uid = $RELATION_TABLE_NAME.friend_id AND friends.group_id = $RUSSIAN_GROUP_IDX
       |WHERE user_id IN (
       |  $friends1stLevelStr
       |) AND is_deleted = FALSE
       |UNION ALL
       |SELECT $FIELDS
       |FROM $RELATION_TABLE_NAME
       |  INNER JOIN $TABLE_NAME ON $TABLE_NAME.uid = $RELATION_TABLE_NAME.user_id AND $TABLE_NAME.group_id = $RUSSIAN_GROUP_IDX
       |WHERE $RELATION_TABLE_NAME.friend_id in (
       |  $friends1stLevelStr
       |) AND is_deleted = FALSE
     """.stripMargin

    val time = System.currentTimeMillis()
    val res = q().list
    System.out.println(" getRussianFriends2ndRound Timing:  " + (System.currentTimeMillis() - time))
    res
  }

  def getAllUserIDs()(implicit db:Database): List[String] = db.withDynSession{
    val q = Q[String] +
    s"""
       |SELECT uid
       |FROM $TABLE_NAME
       |WHERE friends_col = 0
     """.stripMargin
    q().list
  }

  def updateFriendsCnt(user: FriendInfo)(implicit db:Database): Unit = db.withDynSession{
    (Q.u +
      s"""
      |UPDATE $TABLE_NAME
      |SET friends_col = '${user.counters.friends}'
      |WHERE uid = ${user.uid}
       """.stripMargin).execute
  }

  def updateTags(uid: String,tags: String)(implicit db:Database): Unit = db.withDynSession{
    Tags.saveTags(uid,tags)
  }

  def getTags(uid: String)(implicit db:Database): String = db.withDynSession{
    Tags.getAllTags(uid)
  }

  def getUsersByTag(tag: String)(implicit db:Database): List[FriendInfo] = db.withDynSession{
    println("Get users by tag : " + tag)
    val q = Q[(String,String),(String,String,String,String,String,String,String,String,String,String,String)] +
      s"""
         |SELECT $FIELDS,(
         |  SELECT timestamp
         |  FROM ${Tags.RELATION_TABLE_NAME}
         |  WHERE uid = $TABLE_NAME.uid AND
         |  tid IN (
         |    SELECT tid
         |    FROM ${Tags.TABLE_NAME}
         |    WHERE name LIKE ?
         |  )
         |) AS ts
         |FROM $TABLE_NAME WHERE uid IN (
         |  SELECT uid
         |  FROM ${Tags.RELATION_TABLE_NAME}
         |  WHERE
         |    ${Tags.RELATION_TABLE_NAME}.tid IN (
         |      SELECT tid
         |      FROM ${Tags.TABLE_NAME}
         |      WHERE name LIKE ?
         |    )
         |)
         |ORDER BY ts DESC
       """.stripMargin
    q(tag,tag).list
  }

  def getUsersWithTags()(implicit db: Database): List[String] = db.withDynSession{
    val q = Q[String] +
      s"""
         |SELECT uid
         |FROM ${Tags.RELATION_TABLE_NAME}
         |INNER JOIN ${Tags.TABLE_NAME} ON ${Tags.TABLE_NAME}.tid = ${Tags.RELATION_TABLE_NAME}.tid AND ${Tags.TABLE_NAME}.name not like 'в/ч%'
         |GROUP by uid
       """.stripMargin
    q.list
  }

  def getUsersByCityIDs(cid: String)(implicit db:Database): List[FriendInfo] = db.withDynSession{
    val q = Q[String,(String,String,String,String,String,String,String,String,String,String,String)] +
    s"""
       |SELECT $FIELDS
       |FROM $TABLE_NAME
       |WHERE city = ? AND group_id = $RUSSIAN_GROUP_IDX
     """.stripMargin
    q(cid).list
  }

  //We need to eliminate GoogleVisualFeatureDetection , NoPhotosForGoogleDetection tags
  def getUsersWithSimilarTagToUser(uid: String)(implicit db:Database): List[FriendInfo] = db.withDynSession{
    val q = Q[(String,String),(String,String,String,String,String,String,String,String,String,String,String)] +
    s"""
       |SELECT $FIELDS
       |FROM friends
       |WHERE uid IN (
       |  SELECT uid
       |  FROM friend_tag
       |  WHERE tid IN (
       |    SELECT tid FROM friend_tag WHERE uid = ? AND tid NOT IN (272766,717457)
       |  )
       |)
       |AND uid != ?
       |GROUP BY uid
     """.stripMargin
    q(uid,uid).list
  }

  def updateComments(uid: String,comments: String)(implicit db:Database): Unit = db.withDynSession{
    (Q.u + s"UPDATE $TABLE_NAME SET comments = " +? comments + " WHERE uid = " +? uid.toInt).execute
  }

  def updateCoordinatesForChart(uid: String,x: String, y: String)(implicit db: Database) = db.withDynSession{
    (Q.u + s"UPDATE $TABLE_NAME SET x = " +? x.toDouble + ", y = " +? y.toDouble + " WHERE uid = " +? uid.toInt).execute
  }

  def updatePotential()(implicit db: Database) = db.withDynTransaction{
    (Q.u + s"truncate potential_temp_table").execute
    (Q.u +
      s"""
         |insert into potential_temp_table(uid,cnt)
         |  SELECT uid,cnt FROM (
         |                        SELECT
         |                          friends.uid,
         |                          friends.friends_col,
         |                          friends.sex,
         |                          count(*) cnt
         |                        FROM friends_relation
         |                          INNER JOIN friends ON uid = friend_id
         |                        WHERE user_id IN (SELECT uid
         |                                          FROM friends
         |                                          WHERE is_deleted = FALSE AND group_id = '1')
         |                              AND friend_id NOT IN (SELECT uid
         |                                                    FROM friends
         |                                                    WHERE is_deleted = FALSE AND group_id = '1')
         |                              AND is_deleted = FALSE
         |                        GROUP BY friend_id
         |                        HAVING cnt > 3 AND sex = 2 AND friends.friends_col < 1200
         |                      ) q1;
         |
       """.stripMargin).execute
    (Q.u + s"UPDATE friends INNER JOIN potential_temp_table ON friends.uid = potential_temp_table.uid SET is_potential = potential_temp_table.cnt").execute
  }

  def insertPotentialWithPhotosInPolygon(potentialIDs: Set[String])(implicit db: Database): Unit = db.withDynSession{
    (Q.u + s"TRUNCATE $TABLE_NAME_POTENTIAL_WITH_PHOTOS_IN_POLYGON").execute
    val potentialIDsStr = potentialIDs.map("('" + _ + "')").mkString(",")
    (Q.u + s"INSERT INTO $TABLE_NAME_POTENTIAL_WITH_PHOTOS_IN_POLYGON VALUES $potentialIDsStr").execute
  }

}
