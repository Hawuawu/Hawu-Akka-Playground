package com.hawu.playground.akka.tests

import com.hawu.playground.akka.producer.{KafkaMessage, KafkaMessageBuilder}
import org.scalatest.FlatSpec

class KafkaMessageTests extends FlatSpec {
 it should  "serialize and deserialize KafkaMessage " in {
   val message1 = KafkaMessage()
   val message2 = KafkaMessage(1000L, "aaaa", "bbbb", "dddd")

   val serialized = message1.toString
   val deserialized = KafkaMessageBuilder(serialized)

   val serialized2 = message2.toString
   val deserialized2 = KafkaMessageBuilder(serialized2)

   assert(Some(message1) == deserialized)
   assert(Some(message2) == deserialized2)
 }
}
