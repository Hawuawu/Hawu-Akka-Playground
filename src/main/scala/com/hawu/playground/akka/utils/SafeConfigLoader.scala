package com.hawu.playground.akka.utils

import com.typesafe.config.Config

object SafeConfigLoader {
  def apply[T](config: Config, path: String, handle: (Config, String) => T): Option[T] = {
    if (config.hasPathOrNull(path)) {
      if (config.getIsNull(path)) {
        None
      } else {
        Some(handle(config, path))
      }
    } else {
      None
    }
  }
}
