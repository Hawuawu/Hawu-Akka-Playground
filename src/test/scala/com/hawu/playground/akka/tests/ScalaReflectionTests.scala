package com.hawu.playground.akka.tests

import com.hawu.playground.akka.utils.{Serialization, XMLSerializable}
import org.scalatest.FlatSpec

case class Test1(value: java.lang.Byte) extends XMLSerializable

case class Test2(value: java.lang.Short) extends XMLSerializable

case class Test3(value: java.lang.Long) extends XMLSerializable

case class Test4(value: java.lang.Double) extends XMLSerializable

case class Test5(value: java.lang.Character) extends XMLSerializable

case class Test6(value: java.lang.String) extends XMLSerializable

case class Test7(value: java.lang.Boolean) extends XMLSerializable

case class Test8(value: java.lang.Integer) extends XMLSerializable

case class Test9(value: java.lang.Float) extends XMLSerializable

case class Test10(value: scala.Int) extends XMLSerializable

case class Test11(value: scala.Float) extends XMLSerializable

case class Test12(value: scala.Boolean) extends XMLSerializable

case class Test13(value: scala.Char) extends XMLSerializable

case class Test14(value: scala.Long) extends XMLSerializable

case class Test15(value: scala.Byte) extends XMLSerializable

case class Test16(value: scala.Double) extends XMLSerializable

case class Test17() extends XMLSerializable

class ScalaReflectionTests extends FlatSpec {
  it should "serialize and deserialize test message" in {


    val test1 = Test1(10.toByte)
    val test2 = Test2(10.toShort)
    val test3 = Test3(10.toLong)
    val test4 = Test4(10.toDouble)
    val test5 = Test5("a".charAt(0))
    val test6 = Test6(10.toString)
    val test7 = Test7(true)
    val test8 = Test8(10)
    val test9 = Test9(10.toFloat)

    val test10 = Test10(10)
    val test11 = Test11(10.toFloat)
    val test12 = Test12(true)
    val test13 = Test13("a".charAt(0))
    val test14 = Test14(10.toLong)
    val test15 = Test15(10.toByte)
    val test16 = Test16(10.toDouble)
    val test17 = Test17()

    val serilized1 = Serialization(test1)
    val deserialized1 = Serialization(serilized1).asInstanceOf[Option[Test1]]
    assert(Some(test1) == deserialized1)

    val serilized2 = Serialization(test2)
    val deserialized2 = Serialization(serilized2).asInstanceOf[Option[Test2]]
    assert(Some(test2) == deserialized2)

    val serilized3 = Serialization(test3)
    val deserialized3 = Serialization(serilized3).asInstanceOf[Option[Test3]]
    assert(Some(test3) == deserialized3)

    val serilized4 = Serialization(test4)
    val deserialized4 = Serialization(serilized4).asInstanceOf[Option[Test4]]
    assert(Some(test4) == deserialized4)

    val serilized5 = Serialization(test5)
    val deserialized5 = Serialization(serilized5).asInstanceOf[Option[Test5]]
    assert(Some(test5) == deserialized5)

    val serilized6 = Serialization(test6)
    val deserialized6 = Serialization(serilized6).asInstanceOf[Option[Test6]]
    assert(Some(test6) == deserialized6)

    val serilized7 = Serialization(test7)
    val deserialized7 = Serialization(serilized7).asInstanceOf[Option[Test7]]
    assert(Some(test7) == deserialized7)

    val serilized8 = Serialization(test8)
    val deserialized8 = Serialization(serilized8).asInstanceOf[Option[Test8]]
    assert(Some(test8) == deserialized8)

    val serilized9 = Serialization(test9)
    val deserialized9 = Serialization(serilized9).asInstanceOf[Option[Test9]]
    assert(Some(test9) == deserialized9)

    val serilized10 = Serialization(test10)
    val deserialized10 = Serialization(serilized10).asInstanceOf[Option[Test10]]
    assert(Some(test10) == deserialized10)

    val serilized11 = Serialization(test11)
    val deserialized11 = Serialization(serilized11).asInstanceOf[Option[Test11]]
    assert(Some(test11) == deserialized11)

    val serilized12 = Serialization(test12)
    val deserialized12 = Serialization(serilized12).asInstanceOf[Option[Test12]]
    assert(Some(test12) == deserialized12)

    val serilized13 = Serialization(test13)
    val deserialized13 = Serialization(serilized13).asInstanceOf[Option[Test13]]
    assert(Some(test13) == deserialized13)

    val serilized14 = Serialization(test14)
    val deserialized14 = Serialization(serilized14).asInstanceOf[Option[Test14]]
    assert(Some(test14) == deserialized14)

    val serilized15 = Serialization(test15)
    val deserialized15 = Serialization(serilized15).asInstanceOf[Option[Test15]]
    assert(Some(test15) == deserialized15)

    val serilized16 = Serialization(test16)
    val deserialized16 = Serialization(serilized16).asInstanceOf[Option[Test16]]
    assert(Some(test16) == deserialized16)

    val serilized17 = Serialization(test17)
    val deserialized17 = Serialization(serilized17).asInstanceOf[Option[Test17]]
    assert(Some(test17) == deserialized17)


  }
}
