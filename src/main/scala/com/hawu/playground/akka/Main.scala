package com.hawu.playground.akka

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http.ServerBinding
import com.hawu.playground.akka.command._
import com.hawu.playground.akka.consumer.{CommandKafkaConsumer /*, ConsumerFactory /*UNUSED*/ */}
import com.hawu.playground.akka.event.PlaygroundPersistentState
import com.hawu.playground.akka.http.server.RESTServer
import com.hawu.playground.akka.producer.{CommandKafkaProducer /*, ProducerFactory /*UNUSED*/ */}
//import org.apache.kafka.clients.consumer.KafkaConsumer // UNUSED
//import org.apache.kafka.clients.producer.KafkaProducer // UNUSED

import scala.concurrent.Future

case class ApplicationContext(actorSystem: Option[ActorSystem] = None, restServerBinding: Option[Future[ServerBinding]] = None)
object ApplicationContextBuilder {
  def build(actoSystemName: String, https: Boolean = false): ApplicationContext = {
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

    val system = ActorSystem(actoSystemName)
    import scala.concurrent.ExecutionContext.Implicits.global
    system.whenTerminated.onComplete {
      completion =>
        println("Akka playground stopped!")
    }

    val host = system.settings.config.getString("playground.rest.server.host")
    val port = system.settings.config.getInt("playground.rest.server.port")
    val certPass = system.settings.config.getString("playground.cert.pass")

    val persitenceAcor = system.actorOf(Props[PlaygroundPersistentState])

    val commandReplyProxy = system.actorOf(Props[CommandReplyProxy])
    val kafkaProducerActor = system.actorOf(Props(classOf[CommandKafkaProducer]))
    val commandsController = system.actorOf(Props(classOf[CommandController], kafkaProducerActor, commandReplyProxy))

    val commandsReceiver = system.actorOf(Props(classOf[CommandConsumerSideReceiver], persitenceAcor, kafkaProducerActor))
    val repliesReceiver = system.actorOf(Props(classOf[RepliesReceiver], commandReplyProxy))
    system.actorOf(Props(classOf[CommandKafkaConsumer], repliesReceiver, commandsReceiver))

    if(https) {
      val restServerBinding = Some(RESTServer(port, host, commandsController, system, certPass, https))
      ApplicationContext(Some(system), restServerBinding)
    } else {
      val restServerBinding = Some(RESTServer(port, host, commandsController, system))
      ApplicationContext(Some(system), restServerBinding)
    }
  }
}

object Main extends App {

  var context: Option[ApplicationContext] = None

  try {
    context = Some(ApplicationContextBuilder.build("Hawus_space", https = true))
    context.map(ctx => {
      ctx.actorSystem.map(system => {
        system.log.debug("Press enter to quit!")
      })
    })

    scala.io.StdIn.readLine
  } catch {
    case t: Throwable =>
      context.map(ctx => {
        ctx.actorSystem.map(system => {
          system.log.error("Exception while starting entrypoint {}", t)
        })
      })
  }

  context.map(ctx => {
    ctx.restServerBinding.map(boxedBinding => ctx.actorSystem.map(as => {

      implicit val executionContext = as.dispatcher

      boxedBinding
        .flatMap(_.unbind)
        .onComplete(_ =>
          as.terminate
        )
    }))
  })

}
