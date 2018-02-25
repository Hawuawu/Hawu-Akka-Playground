package com.hawu.playground.akka.utils

import com.typesafe.config.Config

object SafeConfigLoader {

  def apply[T](config: Config, path: String, handle: (Config, String) => T): Option[T] = {
    config match {
      case c if (c.hasPathOrNull(path) && !c.getIsNull(path)) => Some(handle(config, path))
      case _ => None
    }
  }
}
