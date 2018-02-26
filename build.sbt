name := "Hawu Akka Playground"

version := "1.0"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
  "com.typesafe.akka" %% "akka-actor" % "2.5.9",
  "com.typesafe.akka" %% "akka-persistence" % "2.5.9",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.10" % Test,
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  "org.apache.spark" %% "spark-core" % "2.2.1",
  "org.apache.kafka" %% "kafka" % "1.0.0",
  "com.typesafe.akka" %% "akka-http" % "10.1.0-RC2",
  "com.typesafe.akka" %% "akka-stream" % "2.5.9",
  "org.scalactic" %% "scalactic" % "3.0.4",
  "org.scalatest" %% "scalatest" % "3.0.4" % Test,
  "io.spray" %% "spray-json" % "1.3.3"
)

scalacOptions in Compile := Seq(
  "-encoding",
  "utf-8",
  "-target:jvm-1.8",
  "-explaintypes",
  "-feature",
  "-unchecked",
  "-deprecation",
  "-language:postfixOps",
  "-language:existentials",
  "-language:experimental.macros",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Xlint",
  "-Xlint:inaccessible",
  "-Xlint:nullary-override",
  "-Xlint:nullary-unit",
  "-Xlint:option-implicit",
  "-Xlint:package-object-classes",
  "-Xlint:poly-implicit-overload",
  "-Xlint:private-shadow",
  "-Xlint:unsound-match",
  "-Xlint:missing-interpolator",
  "-Xfuture",
  "-Yrangepos",
  "-Ydelambdafy:method",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-inaccessible",
  "-Ywarn-value-discard",
  "-Ywarn-unused-import",
  "-Ywarn-unused"
)

scalacOptions in (Compile, console) ~= (_.filterNot(
  Set(
    "-Ywarn-unused:imports"
  )))

scalacOptions in Test := Seq(
  "-Ywarn-unused-import",
  "-Ywarn-unused"
)
