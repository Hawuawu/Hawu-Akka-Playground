package com.hawu.playground.akka.tests

import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import com.hawu.playground.akka.ApplicationContextBuilder
import com.hawu.playground.akka.http.client.RESTClient
import org.scalatest.{AsyncFlatSpec /*, FlatSpec /*UNUSED*/ */}

import scala.concurrent.{/*Await, /*UNUSED*/ */Future}
//import akka.pattern.ask // UNUSED
//import akka.util.Timeout // UNUSED

//import scala.concurrent.duration._ // UNUSED

class GetAllMessagesTest extends AsyncFlatSpec  {

  it should "show all messages" in {
    val ctx = ApplicationContextBuilder.build("Hawus_space2")

    ctx.actorSystem.map(system => {
      val host = system.settings.config.getString("playground.rest.server.host")
      val port = system.settings.config.getInt("playground.rest.server.port")

      val client = new RESTClient(system, "http", host, port)

      client.getAllMessages map {
        case resp: HttpResponse =>
          implicit val actorSystem = system
          //implicit val materializer = ActorMaterializer() // UNUSED

          ActorSystemTerminator(ctx.actorSystem, ctx.restServerBinding)
          assert(resp.status.intValue == 201)
      }
    }).getOrElse(Future { fail }) // FIXME replace with either `Future.successful` or `Future.failed`
  }

}
