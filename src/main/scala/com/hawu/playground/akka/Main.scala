package com.hawu.playground.akka

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import com.hawu.playground.akka.command.CommandController
import com.hawu.playground.akka.http.server.RESTServer

import scala.concurrent.{ExecutionContext, Future}

object Main extends App {
  var actorSystem: Option[ActorSystem] = None
  var restServerBinding: Option[Future[ServerBinding]] = None

  try {
    val system = ActorSystem("Hawus_space")
    actorSystem = Some(system)

    val commanController = system.actorOf(Props(classOf[CommandController]))

    restServerBinding = Some(RESTServer(8080, "localhost", commanController, system))
  } catch {
    case t: Throwable =>
      actorSystem.map(as => as.log.error("Exception while starting entrypoint {}", t))
  }

  actorSystem.map(as => as.log.debug("Press enter to quit!"))
  scala.io.StdIn.readLine
  restServerBinding
    .map(boxedBinding => actorSystem.map(as => {

      implicit val executionContext = as.dispatcher

      boxedBinding
        .flatMap(_.unbind)
        .onComplete(_ => as.terminate)

    }))
}
