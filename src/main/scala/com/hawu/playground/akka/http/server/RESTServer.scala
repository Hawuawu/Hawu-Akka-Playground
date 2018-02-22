package com.hawu.playground.akka.http.server

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._

import scala.concurrent.Future

object RESTServer {
  def apply(
             port: Int,
             host: String,
             commandController: ActorRef,
             as: ActorSystem): Future[ServerBinding] = {

    implicit val actorSystem = as
    implicit val executionContext = as.dispatcher
    implicit val materializer = ActorMaterializer()

    val services = new RESTServices(commandController)
    Http().bindAndHandle(
      services.getMessagesReq ~
        services.getAllMessagesReq ~
        services.deleteGroupReq ~
        services.createGroupReq ~
        services.moveMessageToGroupReq
      , host, port)
  }
}
