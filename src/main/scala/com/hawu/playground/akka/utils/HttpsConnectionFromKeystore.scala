package com.hawu.playground.akka.utils

import java.io.InputStream
import java.security.{KeyStore, SecureRandom}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}

import akka.actor.ActorSystem
//import akka.http.javadsl.HttpsConnectionContext // UNUSED
//import akka.http.scaladsl.ConnectionContext // UNUSED
//import akka.stream.ActorMaterializer // UNUSED

object HttpsConnectionFromKeystore {

  def apply(pass: String, as: ActorSystem): SSLContext = {
    //implicit val system = as // UNUSED
    //implicit val mat = ActorMaterializer() // UNUSED
    //implicit val dispatcher = system.dispatcher // UNUSED

    val password: Array[Char] = pass.toCharArray

    val ks: KeyStore = KeyStore.getInstance("PKCS12")
    val keystore: InputStream = getClass.getClassLoader.getResourceAsStream("server.p12")

    require(keystore != null, "Keystore required!")
    ks.load(keystore, password)

    val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509")
    keyManagerFactory.init(ks, password)

    val tmf: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
    tmf.init(ks)

    val sslContext: SSLContext = SSLContext.getInstance("TLS")
    sslContext.init(keyManagerFactory.getKeyManagers, tmf.getTrustManagers, new SecureRandom)
    sslContext
  }
}

