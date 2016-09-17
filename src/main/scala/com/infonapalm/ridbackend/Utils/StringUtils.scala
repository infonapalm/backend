package com.infonapalm.ridbackend.Utils

/**
 * Created with IntelliJ IDEA.
 * User: infonapalm
 * Date: 5/1/15
 * Time: 4:18 PM

 */
object StringUtils {
  implicit class StringImprovements(val s: String) {
    import scala.util.control.Exception._
    def toDoubleOpt = catching(classOf[NumberFormatException]) opt s.toDouble
    def toDoubleOrZero: Double = toDoubleOpt.getOrElse(0D)
  }
}
