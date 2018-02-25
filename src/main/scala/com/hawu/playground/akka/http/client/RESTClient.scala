package com.hawu.playground.akka.http.client

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpsConnectionContext}
import akka.http.scaladsl.model.{HttpRequest /*, HttpResponse /*UNUSED*/ */}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model.HttpMethods._

import scala.collection.immutable.HashMap
//import scala.concurrent.Future // UNUSED

object RESTClientURLBuilder {

  def apply(
    prefix: String,
    host: String,
    port: Int,
    parameters: HashMap[String, String] = HashMap.empty[String, String]
  ): String = {

    /*
      INFO this was broken producing uris like

      http://localhost:8080
      ?groupId=group&

      thus producing errors like
      [info] DeleteGroupTest:
      [info] - should delete groups *** FAILED ***
      [info]   akka.http.scaladsl.model.IllegalUriException: Illegal URI reference: Invalid input '\n', expected '/', 'EOI', '#', '?' or pchar (line 1, column 25): http://localhost:8080/v1
    */

    val base = f"$prefix%s://$host%s:$port%n".replaceAll("\\s", "")

    if (parameters.isEmpty) {
      return base
    }

    return base + "?" + parameters.map(pair => pair._1 + "=" + pair._2).mkString("&")
  }

}

class RESTClient(actorSystem: ActorSystem, prefix: String, host: String, port: Int) {
  implicit val system = actorSystem
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def getAllMessagesUsingHTTPS(context: HttpsConnectionContext) = {
    Http().setDefaultClientHttpsContext(context)
    Http().singleRequest(HttpRequest(GET, uri = RESTClientURLBuilder(prefix, host, port)))
  }

  // FIXME lines too long (over 120 characters)

  def getAllMessages = Http()
    .singleRequest(HttpRequest(GET, uri = RESTClientURLBuilder(prefix, host, port)))

  def getAllMessagesForGroupId(groupId: String) = Http()
    .singleRequest(HttpRequest(GET, uri = RESTClientURLBuilder(prefix, host, port, HashMap("groupId" -> groupId))))

  def createGroup(groupId: String) = Http()
    .singleRequest(HttpRequest(POST, uri =  RESTClientURLBuilder(prefix, host, port, HashMap("groupId" -> groupId))))

  def deleteGroup(groupId: String) = Http()
    .singleRequest(HttpRequest(DELETE, uri =  RESTClientURLBuilder(prefix, host, port, HashMap("groupId" -> groupId))))

  def assignMessaggeToGroupId(message: String, groupId: String) = Http()
    .singleRequest(HttpRequest(PUT, uri =  RESTClientURLBuilder(prefix, host, port,  HashMap("groupId" -> groupId, "message" -> message))))
}
