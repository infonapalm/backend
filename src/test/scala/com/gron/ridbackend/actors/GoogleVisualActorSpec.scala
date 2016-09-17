package com.infonapalm.ridbackend.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActors, TestKit}
import akka.util.Timeout
import com.infonapalm.ridbackend.ServerMain
import org.scalatest._
import akka.pattern.ask
import akka.routing.RoundRobinPool

import scala.concurrent.Await
import scala.concurrent.duration._

class GoogleVisualActorSpec extends TestKit(ActorSystem("testSystem")) with WordSpecLike with MustMatchers  {

  implicit val timeout = Timeout(5 seconds)

  "Google must return tag " must {
    "tag soldier" in {
      val actor = system.actorOf(RoundRobinPool(1).props(Props[GoogleVisualActor]))
      val result =  ask(actor, GoogleVisualMsg("1","297798536","http://cs633625.vk.me/v633625536/2f901/XJeqHX6DluQ.jpg", true)).mapTo[List[String]]
      val tags = Await.result(result, 5 second)
      tags must equal(List("soldier","tire","wheel"))
    }
  }

}
