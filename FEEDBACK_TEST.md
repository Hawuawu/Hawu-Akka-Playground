```
11050 [Hawus_space5-akka.actor.default-dispatcher-4] DEBUG org.apache.kafka.common.metrics.Metrics  - Added sensor with name records-lag
11051 [Hawus_space5-akka.actor.default-dispatcher-4] WARN  org.apache.kafka.clients.consumer.ConsumerConfig  - The configuration 'auto.commit.enable' was supplied but isn't a known config.
11530 [Hawus_space-akka.actor.default-dispatcher-5] WARN  org.apache.kafka.common.utils.AppInfoParser  - Error registering AppInfo mbean
javax.management.InstanceAlreadyExistsException: kafka.producer:type=app-info,id=HawuAkkaPlayground
  at com.sun.jmx.mbeanserver.Repository.addMBean(Repository.java:437)
  at com.sun.jmx.interceptor.DefaultMBeanServerInterceptor.registerWithRepository(DefaultMBeanServerInterceptor.java:1898)
  at com.sun.jmx.interceptor.DefaultMBeanServerInterceptor.registerDynamicMBean(DefaultMBeanServerInterceptor.java:966)
  at com.sun.jmx.interceptor.DefaultMBeanServerInterceptor.registerObject(DefaultMBeanServerInterceptor.java:900)
  at com.sun.jmx.interceptor.DefaultMBeanServerInterceptor.registerMBean(DefaultMBeanServerInterceptor.java:324)
  at com.sun.jmx.mbeanserver.JmxMBeanServer.registerMBean(JmxMBeanServer.java:522)
  at org.apache.kafka.common.utils.AppInfoParser.registerAppInfo(AppInfoParser.java:62)
  at org.apache.kafka.clients.producer.KafkaProducer.<init>(KafkaProducer.java:427)
  at org.apache.kafka.clients.producer.KafkaProducer.<init>(KafkaProducer.java:291)
  at com.hawu.playground.akka.producer.ProducerFactory$.apply(ProducerFactory.scala:11)
  at com.hawu.playground.akka.producer.CommandKafkaProducer.<init>(CommandKafkaProducer.scala:9)
  at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
  at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
  at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
  at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
  at java.lang.Class.newInstance(Class.java:442)
  at akka.util.Reflect$.instantiate(Reflect.scala:44)
  at akka.actor.NoArgsReflectConstructor.produce(IndirectActorProducer.scala:105)
  at akka.actor.Props.newActor(Props.scala:213)
  at akka.actor.ActorCell.newActor(ActorCell.scala:563)
  at akka.actor.ActorCell.create(ActorCell.scala:589)
  at akka.actor.ActorCell.invokeAll$1(ActorCell.scala:462)
  at akka.actor.ActorCell.systemInvoke(ActorCell.scala:484)
  at akka.dispatch.Mailbox.processAllSystemMessages(Mailbox.scala:282)
  at akka.dispatch.Mailbox.run(Mailbox.scala:223)
  at akka.dispatch.Mailbox.exec(Mailbox.scala:234)
  at akka.dispatch.forkjoin.ForkJoinTask.doExec(ForkJoinTask.java:260)
  at akka.dispatch.forkjoin.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1339)
  at akka.dispatch.forkjoin.ForkJoinPool.runWorker(ForkJoinPool.java:1979)
  at akka.dispatch.forkjoin.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:107)
```

### Not properly closing bindings

```
[ERROR] [02/25/2018 15:02:53.147] [Hawus_space5-akka.actor.default-dispatcher-3] [akka://Hawus_space5/system/IO-TCP/selectors/$a/0] Bind failed for TCP channel on endpoint [localhost/127.0.0.1:8080]
java.net.BindException: Address already in use
  at sun.nio.ch.Net.bind0(Native Method)
  at sun.nio.ch.Net.bind(Net.java:433)
  at sun.nio.ch.Net.bind(Net.java:425)
  at sun.nio.ch.ServerSocketChannelImpl.bind(ServerSocketChannelImpl.java:223)
  at sun.nio.ch.ServerSocketAdaptor.bind(ServerSocketAdaptor.java:74)
  at akka.io.TcpListener.liftedTree1$1(TcpListener.scala:56)
  at akka.io.TcpListener.<init>(TcpListener.scala:53)
  at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
  at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
  at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
  at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
  at akka.util.Reflect$.instantiate(Reflect.scala:65)
  at akka.actor.ArgsReflectConstructor.produce(IndirectActorProducer.scala:96)
  at akka.actor.Props.newActor(Props.scala:213)
  at akka.actor.ActorCell.newActor(ActorCell.scala:563)
  at akka.actor.ActorCell.create(ActorCell.scala:589)
  at akka.actor.ActorCell.invokeAll$1(ActorCell.scala:462)
  at akka.actor.ActorCell.systemInvoke(ActorCell.scala:484)
  at akka.dispatch.Mailbox.processAllSystemMessages(Mailbox.scala:282)
  at akka.dispatch.Mailbox.run(Mailbox.scala:223)
  at akka.dispatch.Mailbox.exec(Mailbox.scala:234)
  at akka.dispatch.forkjoin.ForkJoinTask.doExec(ForkJoinTask.java:260)
  at akka.dispatch.forkjoin.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1339)
  at akka.dispatch.forkjoin.ForkJoinPool.runWorker(ForkJoinPool.java:1979)
  at akka.dispatch.forkjoin.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:107)
```

### Non Cross-Platform newline removal in uris

```
info] - should delete groups *** FAILED ***
[info]   akka.http.scaladsl.model.IllegalUriException: Illegal URI reference: Invalid input '\n', expected 'EOI', DIGIT, '#', '?', '@', path-abempty, pct-encoded or userinfo-char (line 1, column 22): http://localhost:8080
[info]                      ^
[info]   at akka.http.scaladsl.model.IllegalUriException$.apply(ErrorInfo.scala:40)
[info]   at akka.http.scaladsl.model.Uri$.fail(Uri.scala:821)
[info]   at akka.http.impl.model.parser.UriParser.fail(UriParser.scala:81)
[info]   at akka.http.impl.model.parser.UriParser.parseUriReference(UriParser.scala:46)
[info]   at akka.http.scaladsl.model.Uri$.apply(Uri.scala:216)
[info]   at akka.http.scaladsl.model.Uri$.apply(Uri.scala:188)
[info]   at com.hawu.playground.akka.http.client.RESTClient.deleteGroup(RESTClient.scala:51)
[info]   at com.hawu.playground.akka.tests.DeleteGroupTest$$anonfun$1$$anonfun$apply$1.apply(DeleteGroupTest.scala:26)
[info]   at com.hawu.playground.akka.tests.DeleteGroupTest$$anonfun$1$$anonfun$apply$1.apply(DeleteGroupTest.scala:19)
[info]   at scala.Option.map(Option.scala:146)
```

### Timeout on ASK

````
[info] AssignMessageToGroupTest:
[info] com.hawu.playground.akka.tests.AssignMessageToGroupTest *** ABORTED ***
[info]   java.util.concurrent.TimeoutException: Futures timed out after [5 seconds]
[info]   at scala.concurrent.impl.Promise$DefaultPromise.ready(Promise.scala:223)
[info]   at scala.concurrent.impl.Promise$DefaultPromise.result(Promise.scala:227)
[info]   at scala.concurrent.Await$$anonfun$result$1.apply(package.scala:190)
[info]   at scala.concurrent.BlockContext$DefaultBlockContext$.blockOn(BlockContext.scala:53)
[info]   at scala.concurrent.Await$.result(package.scala:190)
[info]   at com.hawu.playground.akka.tests.AssignMessageToGroupTest$$anonfun$1.apply(AssignMessageToGroupTest.scala:27)
[info]   at com.hawu.playground.akka.tests.AssignMessageToGroupTest$$anonfun$1.apply(AssignMessageToGroupTest.scala:18)
[info]   at scala.Option.map(Option.scala:146)
[info]   at com.hawu.playground.akka.tests.AssignMessageToGroupTest.<init>(AssignMessageToGroupTest.scala:18)
[info]   at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
[info]   ...

[info] CreateGroupsTest:
[info] - should create groups *** FAILED ***
[info]   java.util.concurrent.TimeoutException: Futures timed out after [5 seconds]
[info]   at scala.concurrent.impl.Promise$DefaultPromise.ready(Promise.scala:223)
[info]   at scala.concurrent.impl.Promise$DefaultPromise.result(Promise.scala:227)
[info]   at scala.concurrent.Await$$anonfun$result$1.apply(package.scala:190)
[info]   at scala.concurrent.BlockContext$DefaultBlockContext$.blockOn(BlockContext.scala:53)
[info]   at scala.concurrent.Await$.result(package.scala:190)
[info]   at com.hawu.playground.akka.tests.CreateGroupsTest$$anonfun$1$$anonfun$apply$1.apply(CreateGroupsTest.scala:25)
[info]   at com.hawu.playground.akka.tests.CreateGroupsTest$$anonfun$1$$anonfun$apply$1.apply(CreateGroupsTest.scala:17)
[info]   at scala.Option.map(Option.scala:146)
[info]   at com.hawu.playground.akka.tests.CreateGroupsTest$$anonfun$1.apply(CreateGroupsTest.scala:17)
[info]   at com.hawu.playground.akka.tests.CreateGroupsTest$$anonfun$1.apply(CreateGroupsTest.scala:14)
[info]   ...

[info] DeleteGroupTest:
[info] - should delete groups *** FAILED ***
[info]   java.util.concurrent.TimeoutException: Futures timed out after [5 seconds]
[info]   at scala.concurrent.impl.Promise$DefaultPromise.ready(Promise.scala:223)
[info]   at scala.concurrent.impl.Promise$DefaultPromise.result(Promise.scala:227)
[info]   at scala.concurrent.Await$$anonfun$result$1.apply(package.scala:190)
[info]   at scala.concurrent.BlockContext$DefaultBlockContext$.blockOn(BlockContext.scala:53)
[info]   at scala.concurrent.Await$.result(package.scala:190)
[info]   at com.hawu.playground.akka.tests.DeleteGroupTest$$anonfun$1$$anonfun$apply$1.apply(DeleteGroupTest.scala:27)
[info]   at com.hawu.playground.akka.tests.DeleteGroupTest$$anonfun$1$$anonfun$apply$1.apply(DeleteGroupTest.scala:19)
[info]   at scala.Option.map(Option.scala:146)
[info]   at com.hawu.playground.akka.tests.DeleteGroupTest$$anonfun$1.apply(DeleteGroupTest.scala:19)
[info]   at com.hawu.playground.akka.tests.DeleteGroupTest$$anonfun$1.apply(DeleteGroupTest.scala:16)
[info]   ...
```
