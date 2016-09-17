package com.infonapalm.ridbackend.Utils

import scala.util.Random

/**
 * Created with IntelliJ IDEA.
 * User: infonapalm
 * Date: 4/26/15
 * Time: 9:57 AM

 */
object Const {
  val ACCESS_TOKEN = ""
  val ACCESS_TOKEN_2 = ""
  val ACCESS_TOKEN_3 = ""
  val ACCESS_TOKEN_4 = ""
  val ACCESS_TOKEN_5 = ""
  val ACCESS_TOKEN_6 = ""
  val ACCESS_TOKEN_7 = ""
  val ACCESS_TOKEN_8 = ""
  val ACCESS_TOKEN_9 = ""
  val ACCESS_TOKEN_10 = ""
  val ACCESS_TOKEN_11 = ""

  def getRandomToken() = Random.shuffle(List(ACCESS_TOKEN,ACCESS_TOKEN_2,ACCESS_TOKEN_3,ACCESS_TOKEN_4,ACCESS_TOKEN_5,
    ACCESS_TOKEN_6,ACCESS_TOKEN_7,ACCESS_TOKEN_8,ACCESS_TOKEN_9,ACCESS_TOKEN_10,ACCESS_TOKEN_11)).head
}
