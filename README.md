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
, [Unit tests for 201](https://github.com/Hawuawu/Hawu-Akka-Playground/blob/master/src/test/scala/com/hawu/playground/akka/tests/RESTServerTestsStatusOk.scala)
, [Unit tests for 501](https://github.com/Hawuawu/Hawu-Akka-Playground/blob/master/src/test/scala/com/hawu/playground/akka/tests/RESTServerTestsFail.scala)
, [Unit tests for https](https://github.com/Hawuawu/Hawu-Akka-Playground/blob/master/src/test/scala/com/hawu/playground/akka/tests/RESTServerTestsHTTPS.scala)
\]

**Self-signed certificate**

REST service are secured by self-signed certificate which I had to generate and put into keystore.

**Apache Kafka**

Apache kafka consists of 4 parts:
- Cluster supervision app **Zookeeper**
- Kafka server which suplies **Broker**
- Message queue **Producer** which is encapsulated by actor [here](https://github.com/Hawuawu/Hawu-Akka-Playground/blob/master/src/main/scala/com/hawu/playground/akka/producer/CommandKafkaProducer.scala)
- Message queue **Consumer** which is created is encapsulated by actor [here](https://github.com/Hawuawu/Hawu-Akka-Playground/blob/master/src/main/scala/com/hawu/playground/akka/consumer/CommandKafkaConsumer.scala)

Installed local VM - Virtual Box, where I'm running kafka server and zookeper.

Installed CentOS 7, Zookeeper, Apache Kafka and tested
- For development purpose i disabled firewall.

- [Kafka server](https://raw.githubusercontent.com/Hawuawu/Hawu-Akka-Playground/master/src/main/resources/kafka_server.png)
- [Kafka producer](https://raw.githubusercontent.com/Hawuawu/Hawu-Akka-Playground/master/src/main/resources/kafka_test_producer.png)
- [Kafka consumer](https://raw.githubusercontent.com/Hawuawu/Hawu-Akka-Playground/master/src/main/resources/kafka_test_consumer.png)
- Followed article with installation of [kafka server](https://www.vultr.com/docs/how-to-install-apache-kafka-on-centos-7)
- Changed proper ipaddresses to current host	

[Diagram for kafka communication](https://raw.githubusercontent.com/Hawuawu/Hawu-Akka-Playground/master/src/main/resources/Kafka_layer.png)

**Spray json case classes serializer and deserializer**

Serializating commands throught spray json library.

**Config File**

Is located [here](https://github.com/Hawuawu/Hawu-Akka-Playground/blob/master/src/main/resources/application.conf)

**Running in docker**

test

`docker-compose run --rm sbt test`

run

`docker-compose run --rm --service-ports sbt run`

**Whole roundtrip unit test**

_It needs to have broker - kafka server running_

- [All roundtrip tests](https://github.com/Hawuawu/Hawu-Akka-Playground/blob/master/src/test/scala/com/hawu/playground/akka/tests/KafkaServiceTests.scala)

_Improved scala tests for using async flat spec , no more await synchronization, but there are cases when I need synchronization_



