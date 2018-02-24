package com.hawu.playground.akka.utils

import java.io.InputStream
import java.security.{KeyStore, SecureRandom}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}

import akka.actor.ActorSystem
import akka.http.javadsl.HttpsConnectionContext
import akka.http.scaladsl.ConnectionContext
import akka.stream.ActorMaterializer

object HttpsConnectionFromKeystore {
  def apply(pass: String, as: ActorSystem): SSLContext = {
    implicit val system = as
    implicit val mat = ActorMaterializer()
    implicit val dispatcher = system.dispatcher

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

