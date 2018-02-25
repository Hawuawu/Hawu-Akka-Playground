package com.hawu.playground.akka.tests

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.ConnectionContext
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.Timeout
import com.hawu.playground.akka.http.client.RESTClient
import com.hawu.playground.akka.http.server.RESTServer
import com.hawu.playground.akka.tests.dummy.{FailureCommandController, SuccessCommandController}
import org.scalatest.{AsyncFlatSpec, FlatSpec}
import akka.stream.ActorMaterializer
import com.hawu.playground.akka.command._
import com.hawu.playground.akka.utils.HttpsConnectionFromKeystore

import scala.concurrent.Future

import scala.concurrent.Await
import akka.util.Timeout
import scala.concurrent.duration._

object ActorSystemTerminator {
  def apply(actorSystem: Option[ActorSystem], restServerBinding: Option[Future[ServerBinding]]) = {

    import scala.concurrent.ExecutionContext.Implicits.global
    restServerBinding
      .map(boxedBinding => actorSystem.map(as => {

        boxedBinding
          .flatMap(_.unbind)
          .onComplete(_ => as.terminate)

      }))

    if (restServerBinding.isEmpty) {
      actorSystem.map(as => {

        implicit val timeout = Timeout(5 seconds)
        Await.result(as.terminate, timeout.duration)
      })
    }
  }
}

class RESTServerTests extends AsyncFlatSpec  {
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

  "RESTClient" should "send GetMessagesForGroup and get 201 response" in {

    var actorSystem: Option[ActorSystem] = None
    var restServerBinding: Option[Future[ServerBinding]] = None

    try {
      val system = ActorSystem("Hawus_space_test")
      actorSystem = Some(system)

      val commanController = system.actorOf(Props(classOf[SuccessCommandController], List("aa", "bb")))

      restServerBinding = Some(RESTServer(8080, "localhost", commanController, system))


      val client = new RESTClient(system, "http", "localhost", 8080)
      import scala.concurrent.ExecutionContext.Implicits.global

      implicit val timeout = Timeout(5 seconds)
      val future: Future[HttpResponse] = client.getAllMessagesForGroupId("aa")
      future map {
         req => {

           actorSystem.map(as => {
             implicit val actorSystem = as
             implicit val materializer = ActorMaterializer()
             val responseAsString: Future[String] = Unmarshal(req.entity).to[String]
             responseAsString.map(body => assert(body == "aa\nbb\n"))
           })

           ActorSystemTerminator(actorSystem,restServerBinding)
           assert(req.status.intValue == 201)
         }
      }
    } catch {
      case t: Throwable =>
        ActorSystemTerminator(actorSystem,restServerBinding)
        Future {fail}
    }


  }

  "RESTClient" should "send GetAllMessages when none group specified" in {
    var actorSystem: Option[ActorSystem] = None
    var restServerBinding: Option[Future[ServerBinding]] = None

    try {
      val system = ActorSystem("Hawus_space_test")
      actorSystem = Some(system)

      val commanController = system.actorOf(Props(classOf[SuccessCommandController], List("aa", "bb")))

      restServerBinding = Some(RESTServer(8081, "localhost", commanController, system))


      val client = new RESTClient(system, "http", "localhost", 8081)
      import scala.concurrent.ExecutionContext.Implicits.global
      val future: Future[HttpResponse] = client.getAllMessages
      future map {
        req => {
          actorSystem.map(as => {
            implicit val actorSystem = as
            implicit val materializer = ActorMaterializer()
            val responseAsString: Future[String] = Unmarshal(req.entity).to[String]
            responseAsString.map(body => assert(body == "aa\nbb\n"))
          })

          ActorSystemTerminator(actorSystem,restServerBinding)
          assert(req.status.intValue == 201)
        }
      }
    } catch {
      case t: Throwable =>
        ActorSystemTerminator(actorSystem,restServerBinding)
        Future {fail}
    }
  }

  "RESTClient" should "send AssignMessageToGroup on put request and get 201 response" in {

    var actorSystem: Option[ActorSystem] = None
    var restServerBinding: Option[Future[ServerBinding]] = None

    try {
      val system = ActorSystem("Hawus_space_test")
      actorSystem = Some(system)

      val commanController = system.actorOf(Props(classOf[SuccessCommandController], List("aa", "bb")))

      restServerBinding = Some(RESTServer(8082, "localhost", commanController, system))


      val client = new RESTClient(system, "http", "localhost", 8082)
      import scala.concurrent.ExecutionContext.Implicits.global
      val future: Future[HttpResponse] = client.assignMessaggeToGroupId("message", "aa")
      future map {
        req => {
          ActorSystemTerminator(actorSystem,restServerBinding)
          assert(req.status.intValue == 201)
        }
      }
    } catch {
      case t: Throwable =>
        ActorSystemTerminator(actorSystem,restServerBinding)
        Future {fail}
    }
  }

  "RESTClient" should "send AssignMessageToGroup and get failure reason and get 500 response" in {

    var actorSystem: Option[ActorSystem] = None
    var restServerBinding: Option[Future[ServerBinding]] = None

    try {
      val system = ActorSystem("Hawus_space_test")
      actorSystem = Some(system)

      val commanController = system.actorOf(Props(classOf[FailureCommandController],
        "cannotCreateGroupReason", "cannotDeleteGroupReason", "cannotAssignMessageToGroupReason"))

      restServerBinding = Some(RESTServer(8083, "localhost", commanController, system))


      val client = new RESTClient(system, "http", "localhost", 8083)
      import scala.concurrent.ExecutionContext.Implicits.global
      val future: Future[HttpResponse] = client.assignMessaggeToGroupId("message", "aa")
      future map {
        req => {
          actorSystem.map(as => {
            implicit val actorSystem = as
            implicit val materializer = ActorMaterializer()
            val responseAsString: Future[String] = Unmarshal(req.entity).to[String]
            responseAsString.map(body => assert(body == "cannotAssignMessageToGroupReason"))
          })

          ActorSystemTerminator(actorSystem,restServerBinding)
          assert(req.status.intValue == 500)
        }
      }
    } catch {
      case t: Throwable =>
        ActorSystemTerminator(actorSystem,restServerBinding)
        Future {fail}
    }
  }

  "RESTClient" should "send CreateGroup and get 201 response" in {

    var actorSystem: Option[ActorSystem] = None
    var restServerBinding: Option[Future[ServerBinding]] = None

    try {
      val system = ActorSystem("Hawus_space_test")
      actorSystem = Some(system)

      val commanController = system.actorOf(Props(classOf[SuccessCommandController], List("aa", "bb")))

      restServerBinding = Some(RESTServer(8082, "localhost", commanController, system))


      val client = new RESTClient(system, "http", "localhost", 8082)
      import scala.concurrent.ExecutionContext.Implicits.global
      val future: Future[HttpResponse] = client.createGroup("aa")
      future map {
        req => {
          ActorSystemTerminator(actorSystem,restServerBinding)
          assert(req.status.intValue == 201)
        }
      }
    } catch {
      case t: Throwable =>
        ActorSystemTerminator(actorSystem,restServerBinding)
        Future {fail}
    }
  }

  "RESTClient" should "send CreateGroup and get failure reason and get 500 response " in {

    var actorSystem: Option[ActorSystem] = None
    var restServerBinding: Option[Future[ServerBinding]] = None

    try {
      val system = ActorSystem("Hawus_space_test")
      actorSystem = Some(system)

      val commanController = system.actorOf(Props(classOf[FailureCommandController],
        "cannotCreateGroupReason", "cannotDeleteGroupReason", "cannotAssignMessageToGroupReason"))

      restServerBinding = Some(RESTServer(8083, "localhost", commanController, system))


      val client = new RESTClient(system, "http", "localhost", 8083)
      import scala.concurrent.ExecutionContext.Implicits.global
      val future: Future  [HttpResponse] = client.createGroup("aa")
      future map {
        req => {
          actorSystem.map(as => {
            implicit val actorSystem = as
            implicit val materializer = ActorMaterializer()
            val responseAsString: Future[String] = Unmarshal(req.entity).to[String]
            responseAsString.map(body => assert(body == "cannotCreateGroupReason"))
          })

          ActorSystemTerminator(actorSystem,restServerBinding)
          assert(req.status.intValue == 500)
        }
      }
    } catch {
      case t: Throwable =>
        ActorSystemTerminator(actorSystem,restServerBinding)
        Future {fail}
    }
  }

  "RESTClient" should "send DeleteGroup and get 201 response" in {

    var actorSystem: Option[ActorSystem] = None
    var restServerBinding: Option[Future[ServerBinding]] = None

    try {
      val system = ActorSystem("Hawus_space_test")
      actorSystem = Some(system)

      val commanController = system.actorOf(Props(classOf[SuccessCommandController], List("aa", "bb")))

      restServerBinding = Some(RESTServer(8082, "localhost", commanController, system))


      val client = new RESTClient(system, "http", "localhost", 8082)
      import scala.concurrent.ExecutionContext.Implicits.global
      val future: Future[HttpResponse] = client.deleteGroup("aa")
      future map {
        req => {
          ActorSystemTerminator(actorSystem,restServerBinding)
          assert(req.status.intValue == 201)
        }
      }
    } catch {
      case t: Throwable =>
        ActorSystemTerminator(actorSystem,restServerBinding)
        Future {fail}
    }
  }

  "RESTClient" should "send DeleteGroup and get failure reason and get 500 response " in {

    var actorSystem: Option[ActorSystem] = None
    var restServerBinding: Option[Future[ServerBinding]] = None

    try {
      val system = ActorSystem("Hawus_space_test")
      actorSystem = Some(system)

      val commanController = system.actorOf(Props(classOf[FailureCommandController],
        "cannotCreateGroupReason", "cannotDeleteGroupReason", "cannotAssignMessageToGroupReason"))

      restServerBinding = Some(RESTServer(8083, "localhost", commanController, system))


      val client = new RESTClient(system, "http", "localhost", 8083)
      import scala.concurrent.ExecutionContext.Implicits.global
      val future: Future[HttpResponse] = client.deleteGroup("aa")
      future map {
        req => {
          actorSystem.map(as => {
            implicit val actorSystem = as
            implicit val materializer = ActorMaterializer()
            val responseAsString: Future[String] = Unmarshal(req.entity).to[String]
            responseAsString.map(body => assert(body == "cannotDeleteGroupReason"))
          })

          ActorSystemTerminator(actorSystem,restServerBinding) 
          assert(req.status.intValue == 500)
        }
      }
    } catch {
      case t: Throwable =>
        ActorSystemTerminator(actorSystem,restServerBinding)
        Future {fail}
    }
  }

  "RESTClient" should "make secure connection on 443 port" in {

    var actorSystem: Option[ActorSystem] = None
    var restServerBinding: Option[Future[ServerBinding]] = None

    try {
      val system = ActorSystem("Hawus_space_test")
      actorSystem = Some(system)

      val commanController = system.actorOf(Props(classOf[SuccessCommandController], List("aa", "bb")))

      val certPass = system.settings.config.getString("playground.cert.pass")
      restServerBinding = Some(RESTServer(443, "localhost", commanController, system, certPass, true))

      val client = new RESTClient(system, "https", "localhost", 443)
      import scala.concurrent.ExecutionContext.Implicits.global
      val future: Future[HttpResponse] = client.getAllMessagesUsingHTTPS(ConnectionContext.https(HttpsConnectionFromKeystore(certPass, system)))
      future map {
        req => {

          actorSystem.map(as => {
            implicit val actorSystem = as
            implicit val materializer = ActorMaterializer()
            val responseAsString: Future[String] = Unmarshal(req.entity).to[String]
            responseAsString.map(body => assert(body == "aa\nbb\n"))
          })

          ActorSystemTerminator(actorSystem,restServerBinding)
          assert(req.status.intValue == 201)
        }
      }
    } catch {
      case t: Throwable =>
        ActorSystemTerminator(actorSystem,restServerBinding)
        Future {fail}
    }


  }
}
