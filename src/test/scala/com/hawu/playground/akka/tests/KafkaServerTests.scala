package com.hawu.playground.akka.tests

import akka.actor.{ActorSystem, Props}
import com.hawu.playground.akka.consumer.CommandKafkaConsumer
import com.hawu.playground.akka.producer.{CommandKafkaProducer, KafkaMessage, SendKafkaMessageToTopic}
import com.hawu.playground.akka.tests.dummy.{DummyCommandAndRepliesReceiver, WaitForNumberOfMessage, WaitingDone}
import org.scalatest.{AsyncFlatSpec, FlatSpec}
import akka.pattern._
import akka.util.Timeout
import scala.concurrent.duration._

import scala.concurrent.Future

class KafkaServerTests extends AsyncFlatSpec {
  "Kafka producer and consumer" should "send and receive messages" in {
    var actorSystem: Option[ActorSystem] = None

    try {
      val system = ActorSystem("Hawus_space")
      import scala.concurrent.ExecutionContext.Implicits.global
      system.whenTerminated.onComplete {
        completion =>
          println("Akka playground stopped!")
      }

      val dummyCommandAndRepliesReceiver = system.actorOf(Props[DummyCommandAndRepliesReceiver])
      val kafkaConsumerActor = system.actorOf(Props(classOf[CommandKafkaConsumer], dummyCommandAndRepliesReceiver, dummyCommandAndRepliesReceiver))
      val kafkaProducerActor = system.actorOf(Props(classOf[CommandKafkaProducer]))
      val commandTopic = system.settings.config.getString("playground.command.kafka.topic")

      for(i <- 0 to 100) kafkaProducerActor ! SendKafkaMessageToTopic(commandTopic, KafkaMessage(System.currentTimeMillis(), f"$i%n-hash", f"test-message-$i%n"))

      import scala.concurrent.ExecutionContext.Implicits.global
      implicit val timeout = Timeout(5 seconds)
      (dummyCommandAndRepliesReceiver ? WaitForNumberOfMessage(100)) map {
          case WaitingDone => succeed
          case akka.actor.ReceiveTimeout => fail
          case t => fail
      }
    } catch {
      case t: Throwable =>
        actorSystem.map(as => as.log.error("Exception while starting entrypoint {}", t))
        Future{ fail }
    }
 }
}
