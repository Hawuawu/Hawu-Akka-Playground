package com.hawu.playground.akka.tests

import com.hawu.playground.akka.http.client.RESTClientURLBuilder
import org.scalatest.FlatSpec

import scala.collection.immutable.HashMap

class RESTClientURLBuilderTests extends FlatSpec  {
  "RESTClientURLBuilder" should "build proper uri wihout params" in {
    val uri = RESTClientURLBuilder("a", "b", 8080)
    assert(uri.equals("a://b:8080"))
  }


  "RESTClientURLBuilder" should "build proper uri with params" in {
    val uri = RESTClientURLBuilder("a", "b", 8080, HashMap("a" -> "a", "b" -> "b"))
    assert(uri.equals("a://b:8080?a=a&b=b"))
  }

}
