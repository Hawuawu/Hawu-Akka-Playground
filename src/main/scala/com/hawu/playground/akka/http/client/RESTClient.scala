package com.hawu.playground.akka.http.client

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpsConnectionContext}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model.HttpMethods._

import scala.collection.immutable.HashMap
import scala.concurrent.Future

object RESTClientURLBuilder {
 def apply(
            prefix: String,
            host: String,
            port: Int, parameters: HashMap[String, String] = HashMap()): String = {
   var base = f"$prefix%s://$host%s:$port%n".replace("\r\n", "")
   if(!parameters.isEmpty)
     {
       base += "?"
       parameters.map(pair => base += pair._1 + "=" + pair._2 + "&")
       base.substring(0, base.length - 1)
     }
   else {
     base
   }
 }
}

class RESTClient(actorSystem: ActorSystem, prefix: String, host: String, port: Int) {
  implicit val system = actorSystem
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def getAllMessagesUsingHTTPS(context: HttpsConnectionContext) = {
      Http().setDefaultClientHttpsContext(context)
      Http()
        .singleRequest(HttpRequest(GET, uri = RESTClientURLBuilder(prefix, host, port)))
  }

  def getAllMessages = Http()
    .singleRequest(HttpRequest(GET, uri = RESTClientURLBuilder(prefix, host, port)))

  def getAllMessagesForGroupId(groupId: String) = Http()
    .   singleRequest(HttpRequest(GET, uri = RESTClientURLBuilder(prefix, host, port, HashMap("groupId" -> groupId))))

  def createGroup(groupId: String) = Http()
    .singleRequest(HttpRequest(POST, uri =  RESTClientURLBuilder(prefix, host, port, HashMap("groupId" -> groupId))))

  def deleteGroup(groupId: String) = Http()
    .singleRequest(HttpRequest(DELETE, uri =  RESTClientURLBuilder(prefix, host, port, HashMap("groupId" -> groupId))))

  def assignMessaggeToGroupId(message: String, groupId: String) = Http()
    .singleRequest(HttpRequest(PUT, uri =  RESTClientURLBuilder(prefix, host, port,  HashMap("groupId" -> groupId, "message" -> message))))
}
