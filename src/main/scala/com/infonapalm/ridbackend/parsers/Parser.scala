package com.infonapalm.ridbackend.parsers

/**
 * Created with IntelliJ IDEA.
 * User: infonapalm
 * Date: 5/1/15
 * Time: 6:42 PM

 */
class Parser {
  val MAXIMUM_RETRY = 100000000
  
  @annotation.tailrec
  final def retry[T](n: Int)(fn: => T): T = {
    util.Try { fn } match {
      case util.Success(x) => x
      case _ if n > 1 => Thread.sleep(10000);retry(n - 1)(fn)
      case util.Failure(e) => throw e
    }
  }
  
  final def retry[T](fn: => T): T = retry(MAXIMUM_RETRY)(fn)
}
