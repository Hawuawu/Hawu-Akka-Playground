package com.hawu.playground.akka.tests

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.ConnectionContext
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.hawu.playground.akka.command._
import com.hawu.playground.akka.http.client.RESTClient
import com.hawu.playground.akka.http.server.RESTServer
import com.hawu.playground.akka.tests.dummy.SuccessCommandController
import com.hawu.playground.akka.utils.HttpsConnectionFromKeystore
import org.scalatest.{AsyncFlatSpec, BeforeAndAfterAll}

import scala.concurrent.Await
import scala.concurrent.duration._

class RESTServerTestsHTTPS
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

  val certPass = system.settings.config.getString("playground.cert.pass")
  val host = system.settings.config.getString("playground.rest.server.host")
  val port = 443

  val commandController = system.actorOf(Props(classOf[SuccessCommandController], List("aa", "bb")))

  val client = new RESTClient(system, "http", host, port)
  val serverBinding = RESTServer(port, host, commandController, system)

  "RESTClient" should "make secure connection on 443 port" in {
    client.getAllMessagesUsingHTTPS(ConnectionContext.https(HttpsConnectionFromKeystore(certPass, system))) map {
        req => {
          Unmarshal(req.entity).to[String].map(body => assert(body == "aa\nbb\n"))
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



