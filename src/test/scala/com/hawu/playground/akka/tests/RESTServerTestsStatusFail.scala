package com.hawu.playground.akka.tests

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.hawu.playground.akka.command._
import com.hawu.playground.akka.http.client.RESTClient
import com.hawu.playground.akka.http.server.RESTServer
import com.hawu.playground.akka.tests.dummy.{FailureCommandController}
import org.scalatest.{AsyncFlatSpec, BeforeAndAfterAll}

import scala.concurrent.Await
import scala.concurrent.duration._

class RESTServerTestsStatusFail
  extends AsyncFlatSpec
    with BeforeAndAfterAll {

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

  val system = ActorSystem("Hawus_space_test")

  implicit val actorSystem = system
  implicit val materializer = ActorMaterializer()
  implicit val timeout = Timeout(5 seconds)


  val host = system.settings.config.getString("playground.rest.server.host")
  val port = 8080

  val commandController = system.actorOf(Props(classOf[FailureCommandController],
    "cannotCreateGroupReason", "cannotDeleteGroupReason", "cannotAssignMessageToGroupReason"))

  val client = new RESTClient(system, "http", host, port)
  val serverBinding = RESTServer(port, host, commandController, system)

  "RESTClient" should "send AssignMessageToGroup and get failure reason and get 501 response" in {
    client.assignMessaggeToGroupId("message", "aa") map {
      req => {
        Unmarshal(req.entity).to[String].map(body => assert(body == "cannotAssignMessageToGroupReason"))
        assert(req.status.intValue == 501)
      }
    }
  }

  "RESTClient" should "send CreateGroup and get failure reason and get 501 response " in {
    client.createGroup("aa") map {
      req => {
        Unmarshal(req.entity).to[String] map (body => assert(body == "cannotCreateGroupReason"))
        assert(req.status.intValue == 501)
      }
    }
  }

  "RESTClient" should "send DeleteGroup and get failure reason and get 500 response " in {
      client.deleteGroup("aa") map {
        req => {
          Unmarshal(req.entity).to[String].map(body => assert(body == "cannotDeleteGroupReason"))
          assert(req.status.intValue == 501)
        }
      }
  }

  override def afterAll: Unit = {
    Await.result(serverBinding, timeout.duration)
    Await.result(system.terminate, timeout.duration)
    ()
  }
}
