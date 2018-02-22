package com.hawu.playground.akka.http.server

import akka.actor.{Actor, ActorLogging, ActorRef, ReceiveTimeout}
import akka.http.scaladsl.server.directives.{FutureDirectives, OnSuccessMagnet}
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse}
import akka.http.scaladsl.server.{Directive, Directives}
import com.hawu.playground.akka.command._
import com.hawu.playground.akka.event.CreatedGroup

import scala.concurrent.Future
import akka.pattern.ask
import akka.stream.scaladsl.JavaFlowSupport.Source
import akka.util.Timeout

import scala.concurrent.duration._


class RESTServices(commandController: ActorRef) extends FutureDirectives with Directives {

  implicit val timeout = Timeout(5 seconds)

  val getMessagesReq = path("") {
    get {
      parameter('groupId) { groupId =>
        onSuccess(commandController ? GetMessagesForGroup(groupId)) {
          case msg: GotMessages =>
            complete(HttpResponse(201, entity = msg.messages.foldLeft("")(_ + " " + _ + "\n")))

          case anyOther =>
            complete(HttpResponse(500, entity = "Got bad response!" + anyOther))
        }
      }
    }
  }

  val getAllMessagesReq = path("") {
    get {
        onSuccess(commandController ? GetAllMessages) {
          case msg: GotMessages =>
            complete(HttpResponse(201, entity = msg.messages.foldLeft("")(_ + " " + _ + "\n")))

          case anyOther =>
            complete(HttpResponse(500, entity = "Got bad response!" + anyOther))
        }
    }
  }


  val deleteGroupReq = path("") {
    delete {
      parameter('groupId) { groupId =>
        onSuccess(commandController ? DeleteGroupById(groupId)) {
          case msg: GroupByIdDeleted =>
            complete(HttpResponse(201))

          case msg: CannotDeleteGroupById =>
            complete(HttpResponse(500, entity = msg.reason))

          case anyOther =>
            complete(HttpResponse(500, entity = "Got bad response!" + anyOther))
        }
      }
    }
  }

  val createGroupReq = path("") {
    post {
      parameter('groupId) { groupId =>
        onSuccess(commandController ? CreateGroup(groupId)) {
          case msg: GroupCreated =>
            complete(HttpResponse(201))

          case msg: CannotCreateGroup =>
            complete(HttpResponse(500, entity = msg.reason))

          case anyOther =>
            complete(HttpResponse(500, entity = "Got bad response!" + anyOther))
        }
      }
    }
  }

  val moveMessageToGroupReq = path("") {
    put {
      parameters('groupId, 'message) { (groupId, message) =>
        onSuccess(commandController ? AssignMessageToGroup(groupId, message)) {
          case msg: AssignMessageToGroupCompleted =>
            complete(HttpResponse(201))

          case msg: AssignMessageToGroupFailed =>
            complete(HttpResponse(500, entity = msg.reason))

          case anyOther =>
            complete(HttpResponse(500, entity = "Got bad response! " + anyOther))
        }
      }
    }
  }

}