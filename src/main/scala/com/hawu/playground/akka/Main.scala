package com.hawu.playground.akka

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http.ServerBinding
import com.hawu.playground.akka.command._
import com.hawu.playground.akka.consumer.{CommandKafkaConsumer, ConsumerFactory}
import com.hawu.playground.akka.event.PlaygroundPersistentState
import com.hawu.playground.akka.http.server.RESTServer
import com.hawu.playground.akka.producer.{CommandKafkaProducer, ProducerFactory}
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer

import scala.concurrent.Future

object Main extends App {
  var actorSystem: Option[ActorSystem] = None
  var restServerBinding: Option[Future[ServerBinding]] = None

  try {
    CommandsRegisty (
      Seq(
        GetAllMessages(),
        GetMessagesForGroup(),
        DeleteGroupById(),
        GotMessages(),
        AssignMessageToGroup(),
        AssignMessageToGroupCompleted(),
        AssignMessageToGroupFailed(),
        GroupByIdDeleted(),
        CannotDeleteGroupById(),
        CreateGroup(),
        GroupCreated(),
        CannotCreateGroup()
      )
    )

    val system = ActorSystem("Hawus_space")
    import scala.concurrent.ExecutionContext.Implicits.global
    system.whenTerminated.onComplete {
      completion =>
        println("Akka playground stopped!")
    }

    val host = system.settings.config.getString("playground.rest.server.host")
    val port = system.settings.config.getInt("playground.rest.server.port")
    val certPass = system.settings.config.getString("playground.cert.pass")

    actorSystem = Some(system)


    val persitenceAcor = system.actorOf(Props[PlaygroundPersistentState])

    val commandReplyProxy = system.actorOf(Props[CommandReplyProxy])
    val kafkaProducerActor = system.actorOf(Props(classOf[CommandKafkaProducer]))
    val commandsController = system.actorOf(Props(classOf[CommandController], kafkaProducerActor, commandReplyProxy))

    val commandsReceiver = system.actorOf(Props(classOf[CommandConsumerSideReceiver], persitenceAcor, kafkaProducerActor))
    val repliesReceiver = system.actorOf(Props(classOf[RepliesReceiver], commandReplyProxy))
    system.actorOf(Props(classOf[CommandKafkaConsumer], repliesReceiver, commandsReceiver))

    Some(RESTServer(port, host, commandsController, system, certPass, true))

    system.log.debug("Press enter to quit!")
    scala.io.StdIn.readLine
  } catch {
    case t: Throwable =>
      actorSystem.map(as => as.log.error("Exception while starting entrypoint {}", t))
  }

  restServerBinding
    .map(boxedBinding => actorSystem.map(as => {

      implicit val executionContext = as.dispatcher

      boxedBinding
        .flatMap(_.unbind)
        .onComplete(_ =>
          as.terminate
        )
    }))
}
