package com.hawu.playground.akka.tests

import com.hawu.playground.akka.producer.{KafkaMessage, KafkaMessageBuilder}
import org.scalatest.FlatSpec

class KafkaMessageTests extends FlatSpec {

 it should  "serialize and deserialize KafkaMessage " in {

    val serializeAndMatch = (msg: KafkaMessage) =>
      assert(Some(msg) == KafkaMessageBuilder(msg.toString))

    serializeAndMatch(KafkaMessage())
    serializeAndMatch(KafkaMessage(1000L, "aaaa", "bbbb", "dddd"))
 }

}
