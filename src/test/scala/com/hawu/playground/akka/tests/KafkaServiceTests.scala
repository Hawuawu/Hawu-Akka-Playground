package com.hawu.playground.akka.tests

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import com.hawu.playground.akka.http.client.RESTClient
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import com.hawu.playground.akka.command._
import com.hawu.playground.akka.consumer.CommandKafkaConsumer
import com.hawu.playground.akka.event.PlaygroundPersistentState
import com.hawu.playground.akka.http.server.RESTServer
import com.hawu.playground.akka.producer.CommandKafkaProducer

import scala.concurrent.{Await}
import scala.concurrent.duration._


class KafkaServiceTests
  extends TestKit(ActorSystem("Hawu_test_kafka_services"))
    with ImplicitSender
    with WordSpecLike
    with BeforeAndAfterAll
    with Matchers {

  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(5 seconds)

  val host = system.settings.config.getString("playground.rest.server.host")
  val port = 8082

  val commandReplyProxy = system.actorOf(Props[CommandReplyProxy])
  val kafkaProducerActor = system.actorOf(Props(classOf[CommandKafkaProducer]))
  val commandsController = system.actorOf(Props(classOf[CommandController], kafkaProducerActor, commandReplyProxy))

  val persitenceAcor = system.actorOf(Props[PlaygroundPersistentState])

  val commandsReceiver = system.actorOf(Props(classOf[CommandConsumerSideReceiver], persitenceAcor, kafkaProducerActor))
  val repliesReceiver = system.actorOf(Props(classOf[RepliesReceiver], commandReplyProxy))
  system.actorOf(Props(classOf[CommandKafkaConsumer], repliesReceiver, commandsReceiver))

  var client = new RESTClient(system, "http", host, port)
  var serverBinding = RESTServer(port, host, commandsController, system, usingHtps = false)

  override def beforeAll(): Unit = {
    CommandsRegisty(
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
  }

  "Application " must {
    "return 501 when there group doesnt exist for AssignMessaggeToGroupId" in {
      Await.result(client.assignMessaggeToGroupId("message", "group"), timeout.duration) match {
        case resp: HttpResponse =>
          assert(resp.status.intValue == 501)
      }
    }
  }

  "Application " must {
    "return 501 when there group doesnt exist for DeleteGroup" in {
      Await.result(client.deleteGroup("group"), timeout.duration) match {
        case resp: HttpResponse =>
          assert(resp.status.intValue == 501)
      }
    }
  }

  "Application " must {
    "create group after CreateGroup request" in {
      Await.result(client.createGroup("group"), timeout.duration) match {
        case resp: HttpResponse =>
          assert(resp.status.intValue == 201)
      }
    }
  }

  "Application " must {
    "assign message to group when group existss" in {
      Await.result(client.assignMessaggeToGroupId("message", "group"), timeout.duration) match {
        case resp: HttpResponse =>
          assert(resp.status.intValue == 201)
      }
    }
  }

  "Application " must {
    "show messages only for group specified" in {

      Await.result(client.createGroup("group2"), timeout.duration) match {
        case resp: HttpResponse =>
          assert(resp.status.intValue == 201)
      }

      Await.result(client.assignMessaggeToGroupId("message", "group2"), timeout.duration) match {
        case resp: HttpResponse =>
          assert(resp.status.intValue == 201)
      }

      Await.result(client.getAllMessagesForGroupId("group2"), timeout.duration) match {
        case resp: HttpResponse =>
          assert(resp.status.intValue == 201)

          Unmarshal(resp.entity).to[String] map {
            body => assert(body == "message2\n")
          }
      }
    }
  }

  "Application " must {
    "show all messages " in {

      Await.result(client.getAllMessages, timeout.duration) match {
        case resp: HttpResponse =>
          assert(resp.status.intValue == 201)

          Unmarshal(resp.entity).to[String] map {
            body => assert(body == "message\nmessage2\n")
          }
      }
    }
  }

  override def afterAll: Unit = {
    Await.result(serverBinding, timeout.duration)
    shutdown()
    ()
  }

}
