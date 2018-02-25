package com.hawu.playground.akka.utils

import java.util.Properties

import com.typesafe.config.Config

object ConfConverter {

  def toProperties(conf: Config): Properties = {
    import scala.collection.JavaConversions._
    val props = new Properties()

    val converted: Map[String, Object] = conf.entrySet.map(entry =>
      entry.getKey -> entry.getValue.unwrapped()
    )(collection.breakOut)

    props.putAll(converted)
    props
  }

}
