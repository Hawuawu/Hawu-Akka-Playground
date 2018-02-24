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
import com.hawu.playground.akka.producer.KafkaMessage
import com.hawu.playground.akka.utils.Serialization

import scala.concurrent.duration._


class RESTServices(commandController: ActorRef) extends FutureDirectives with Directives {

  implicit val timeout = Timeout(5 seconds)

  val getMessagesReq = path("") {
    get {
      parameter('groupId) { groupId =>
        onSuccess(commandController ? GetMessagesForGroup(groupId)) {
          case msg: KafkaMessage =>  CommandsRegisty(msg.objectName).map(commandFound => {
            Serialization(commandFound.asInstanceOf[KafkaJSONDeserializable], msg.serializedMessage).map(m =>{
              complete(HttpResponse(201, entity =
                m.asInstanceOf[GotMessages].messages
                  .foldLeft("")(_ + " " + _ + "\n")))
            }).getOrElse ({
              complete(HttpResponse(500, entity = "Got bad response!" + msg.serializedMessage))
            })
          }).getOrElse ({
            complete(HttpResponse(500, entity = "Got bad response!" + msg.serializedMessage))
          })

          case CommandTimeouted =>
            complete(HttpResponse(408))

          case anyOther =>
            complete(HttpResponse(500, entity = "Got bad response!" + anyOther))
        }
      }
    }
  }

  val getAllMessagesReq = path("") {
    get {
        onSuccess(commandController ? GetAllMessages()) {
          case msg: KafkaMessage =>
            CommandsRegisty(msg.objectName).map(commandFound => {
              Serialization(commandFound.asInstanceOf[KafkaJSONDeserializable], msg.serializedMessage).map(m =>{
                complete(HttpResponse(201, entity =
                  m.asInstanceOf[GotMessages].messages
                    .foldLeft("")(_ + " " + _ + "\n")))
              }).getOrElse ({
                complete(HttpResponse(500, entity = "Got bad response!" + msg.serializedMessage))
              })
            }).getOrElse ({
              complete(HttpResponse(500, entity = "Got bad response!" + msg.serializedMessage))
            })

          case CommandTimeouted =>
            complete(HttpResponse(408))

          case anyOther =>
            complete(HttpResponse(500, entity = "Got bad response!" + anyOther))
        }
    }
  }

  val deleteGroupReq = path("") {
    delete {
      parameter('groupId) { groupId =>
        onSuccess(commandController ? DeleteGroupById(groupId)) {
          case msg: KafkaMessage =>
          CommandsRegisty(msg.objectName).map(commandFound => {
            Serialization(commandFound.asInstanceOf[KafkaJSONDeserializable], msg.serializedMessage).map(m =>{
              m match {
                case newMsg: CannotDeleteGroupById =>
                  complete(HttpResponse(500, entity = newMsg.reason))

                case newMsg: GroupByIdDeleted =>
                  complete(HttpResponse(201))
              }
            }).getOrElse ({
              complete(HttpResponse(500, entity = "Got bad response!" + msg.serializedMessage))
            })
          }).getOrElse ({
            complete(HttpResponse(500, entity = "Got bad response!" + msg.serializedMessage))
          })

          case msg: CannotDeleteGroupById =>
            complete(HttpResponse(500, entity = msg.reason))

          case CommandTimeouted =>
            complete(HttpResponse(408))


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

          case msg: KafkaMessage =>
            CommandsRegisty(msg.objectName).map(commandFound => {
              Serialization(commandFound.asInstanceOf[KafkaJSONDeserializable], msg.serializedMessage).map(m =>{
                m match {
                  case newMsg: CannotCreateGroup =>
                    complete(HttpResponse(500, entity = newMsg.reason))

                  case newMsg: GroupCreated =>
                    complete(HttpResponse(201))
                }
              }).getOrElse ({
                complete(HttpResponse(500, entity = "Got bad response!" + msg.serializedMessage))
              })
            }).getOrElse ({
              complete(HttpResponse(500, entity = "Got bad response!" + msg.serializedMessage))
            })

          case CommandTimeouted =>
            complete(HttpResponse(408))

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
          case msg: KafkaMessage =>
          CommandsRegisty(msg.objectName).map(commandFound => {
            Serialization(commandFound.asInstanceOf[KafkaJSONDeserializable], msg.serializedMessage).map(m =>{
              m match {
                case newMsg: AssignMessageToGroupFailed =>
                  complete(HttpResponse(500, entity = newMsg.reason))

                case newMsg: AssignMessageToGroupCompleted =>
                  complete(HttpResponse(201))
              }
            }).getOrElse ({
              complete(HttpResponse(500, entity = "Got bad response!" + msg.serializedMessage))
            })
          }).getOrElse ({
            complete(HttpResponse(500, entity = "Got bad response!" + msg.serializedMessage))
          })

          case CommandTimeouted =>
            complete(HttpResponse(408))

          case anyOther =>
            complete(HttpResponse(500, entity = "Got bad response! " + anyOther))
        }
      }
    }
  }

}