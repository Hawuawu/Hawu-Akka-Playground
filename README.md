# Hawu-Akka-Playground
Repository for playing with akka-http, akka-persistence, akka-streams, apache-kaffka, apache-spark, Asynchronous Scala unit tests and so on...


**Akka-http**
Using for REST services which are:
- host:port - GET
- host:port - GET with groupid param 
- host:port - POST with groupId param
- host:port - DELETE with groupId param
- host:port - PUT with groupId and message param


You can find RESTServer services definition at following url's:
 \[
 [RESTServer](https://github.com/Hawuawu/Hawu-Akka-Playground/blob/master/src/main/scala/com/hawu/playground/akka/http/server/RESTServer.scala)
 , [RESTService](https://github.com/Hawuawu/Hawu-Akka-Playground/blob/master/src/main/scala/com/hawu/playground/akka/http/server/RESTServices.scala)
, [Unit tests](https://github.com/Hawuawu/Hawu-Akka-Playground/blob/master/src/test/scala/com/hawu/playground/akka/tests/RESTServerTests.scala)
\]

**Apache Kafka**

Apache kafka consists of 4 parts:
- Cluster supervision app **Zookeeper**
- Kafka server which suplies **Broker**'s
- Message queue **Producer** which is encapsulated by actor [here](https://github.com/Hawuawu/Hawu-Akka-Playground/blob/master/src/main/scala/com/hawu/playground/akka/producer/CommandKafkaProducer.scala)
- Message queue **Consumer** which is created is encapsulated by actor [here](https://github.com/Hawuawu/Hawu-Akka-Playground/blob/master/src/main/scala/com/hawu/playground/akka/consumer/CommandKafkaConsumer.scala)

Installed local VM - Virtual Box, where I'm running kafka server and zookeper.

[Diagram for kafka communication](https://raw.githubusercontent.com/Hawuawu/Hawu-Akka-Playground/master/src/main/resources/Kafka_layer.png)

**Config File**

Is located [here](https://github.com/Hawuawu/Hawu-Akka-Playground/blob/master/src/main/resources/application.conf)


