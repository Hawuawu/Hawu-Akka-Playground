package com.hawu.playground.akka.tests

import akka.http.scaladsl.model.HttpResponse
import org.scalatest.AsyncFlatSpec

import scala.concurrent.{Await, Future}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.hawu.playground.akka.ApplicationContextBuilder
import com.hawu.playground.akka.http.client.RESTClient

import scala.concurrent.duration._


class ShowMessagesForGroupTest extends AsyncFlatSpec {
  it should "show message for group" in {
    val ctx = ApplicationContextBuilder.build("Hawus_space2")

    ctx.actorSystem.map(system => {
      val host = system.settings.config.getString("playground.rest.server.host")
      val port = system.settings.config.getInt("playground.rest.server.port")

      val client = new RESTClient(system, "http", host, port)

      client.getAllMessagesForGroupId("group") map {
        case resp: HttpResponse =>
          implicit val actorSystem = system
          implicit val materializer = ActorMaterializer()

          ActorSystemTerminator(ctx.actorSystem, ctx.restServerBinding)
          assert(resp.status.intValue == 201)
      }
    }).getOrElse(Future{fail})
  }
}
