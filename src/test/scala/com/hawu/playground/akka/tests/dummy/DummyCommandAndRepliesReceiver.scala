package com.hawu.playground.akka.tests.dummy

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.hawu.playground.akka.consumer.KafkaSerializedMessage
import scala.concurrent.duration._


case class  WaitForNumberOfMessage(number: Int)
case object WaitingDone

class DummyCommandAndRepliesReceiver extends Actor with ActorLogging {
  context.setReceiveTimeout(10 seconds)

  var counter = 0
  var required = Int.MaxValue
  var replyTo: Option[ActorRef] = None


   def receive = {
     case akka.actor.ReceiveTimeout =>
       replyTo.map(rp => akka.actor.ReceiveTimeout)

     case WaitForNumberOfMessage(number) =>
       required = number
      replyTo = Some(sender)
       test

    case msg: KafkaSerializedMessage =>
      log.debug("Dummy received message {}", msg.message)
       counter += 1
       test
  }

  def test: Unit = {
    if(counter >= required)
      {
        replyTo.map(rp => rp ! WaitingDone)
      }
  }
}
