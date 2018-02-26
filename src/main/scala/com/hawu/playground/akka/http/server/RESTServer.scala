package com.hawu.playground.akka.http.server

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.{ConnectionContext, Http}
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import com.hawu.playground.akka.utils.HttpsConnectionFromKeystore

import scala.concurrent.Future

object RESTServer {
  def apply(
             port: Int,
             host: String,
             commandController: ActorRef,
             as: ActorSystem,
             sslPass: String = "",
             usingHtps: Boolean = false): Future[ServerBinding] = {

    implicit val actorSystem = as
    implicit val executionContext = as.dispatcher
    implicit val materializer = ActorMaterializer()

    val services = new RESTServices(commandController)

    if (usingHtps) {
      Http().setDefaultServerHttpContext(ConnectionContext.https(HttpsConnectionFromKeystore(sslPass, as)))
    }

    Http().bindAndHandle(
      pathEndOrSingleSlash {
        services.getMessagesReq ~
        services.getAllMessagesReq ~
        services.deleteGroupReq ~
        services.createGroupReq ~
        services.moveMessageToGroupReq
      },
      host, port)
  }
}
