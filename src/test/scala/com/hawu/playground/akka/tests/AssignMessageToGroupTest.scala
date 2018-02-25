package com.hawu.playground.akka.tests

import akka.http.scaladsl.model.HttpResponse
import com.hawu.playground.akka.ApplicationContextBuilder
import com.hawu.playground.akka.http.client.RESTClient
import org.scalatest.AsyncFlatSpec

import scala.concurrent.{Await, Future}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.duration._

class AssignMessageToGroupTest extends AsyncFlatSpec {
  val ctx = ApplicationContextBuilder.build("Hawus_space5")

  ctx.actorSystem.map(system => {
    val host = system.settings.config.getString("playground.rest.server.host")
    val port = system.settings.config.getInt("playground.rest.server.port")

    val client = new RESTClient(system, "http", host, port)

    val future = client.assignMessaggeToGroupId("message", "group")

    implicit val timeout = Timeout(5 seconds)
    Await.result(future, timeout.duration) match {
      case resp: HttpResponse =>
        assert(resp.status.intValue == 501)
    }

    val future2 = client.createGroup("group")
    Await.result(future2, timeout.duration) match {
      case resp: HttpResponse =>
        assert(resp.status.intValue == 201)
    }

    val future3 = client.assignMessaggeToGroupId("group", "message")
    future3 map {
      case resp: HttpResponse =>
        implicit val actorSystem = system
        implicit val materializer = ActorMaterializer()

        ActorSystemTerminator(ctx.actorSystem, ctx.restServerBinding)
        assert(resp.status.intValue == 201)
    }
  }).getOrElse(Future {fail})
}
