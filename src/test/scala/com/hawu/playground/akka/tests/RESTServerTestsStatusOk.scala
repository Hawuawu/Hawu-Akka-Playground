package com.hawu.playground.akka.tests

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.hawu.playground.akka.http.client.RESTClient
import com.hawu.playground.akka.http.server.RESTServer
import com.hawu.playground.akka.tests.dummy.{ SuccessCommandController}
import org.scalatest.{AsyncFlatSpec, BeforeAndAfterAll}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.hawu.playground.akka.command._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class RESTServerTestsStatusOk
  extends AsyncFlatSpec
  with BeforeAndAfterAll {

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

  val system = ActorSystem("Hawus_space_test")

  implicit val actorSystem = system
  implicit val materializer = ActorMaterializer()
  implicit val timeout = Timeout(5 seconds)

  val host = system.settings.config.getString("playground.rest.server.host")
  val port = 8081

  val commandController = system.actorOf(Props(classOf[SuccessCommandController], List("aa", "bb")))

  val client = new RESTClient(system, "http", host, port)
  val serverBinding = RESTServer(port, host, commandController, system)

  "RESTClient" should "send GetMessagesForGroup and get 201 response" in {
    client.getAllMessagesForGroupId("aa") map {
      req => {
        val responseAsString: Future[String] = Unmarshal(req.entity).to[String]

        responseAsString.map(body => assert(body == "aa\nbb\n"))
        assert(req.status.intValue == 201)
      }
    }
  }

  "RESTClient" should "send GetAllMessages when none group specified" in {
    client.getAllMessages map {
      req => {
        val responseAsString: Future[String] = Unmarshal(req.entity).to[String]
        responseAsString.map(body => assert(body == "aa\nbb\n"))

        assert(req.status.intValue == 201)
      }
    }
  }

  "RESTClient" should "send AssignMessageToGroup on put request and get 201 response" in {
    client.assignMessaggeToGroupId("message", "aa") map {
      req => {
        assert(req.status.intValue == 201)
      }
    }
  }

  "RESTClient" should "send CreateGroup and get 201 response" in {
    client.createGroup("aa") map {
        req  => {
          assert(req.status.intValue == 201)
        }
    }
  }

  "RESTClient" should "send DeleteGroup and get 201 response" in {
    client.deleteGroup("aa") map {
      req => {
        assert(req.status.intValue == 201)
      }
    }
  }

  override def afterAll: Unit = {
    Await.result(serverBinding, timeout.duration)
    Await.result(system.terminate, timeout.duration)
    ()
  }
}
