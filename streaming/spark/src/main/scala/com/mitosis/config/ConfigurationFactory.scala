package com.mitosis.config

import com.mitosis.config.objects.Config
import com.typesafe.config.ConfigBeanFactory
import com.typesafe.config.ConfigFactory

import java.io.File
import java.io.InputStreamReader

object ConfigurationFactory {

  /**
   * Loads configuration object by file
   *
   * @return Returns configuration objects
   */
  def load(): Config = {
    val is = new InputStreamReader(getClass.getResourceAsStream(s"/app.conf"))
    val config: com.typesafe.config.Config = ConfigFactory.parseReader(is).resolve()
    ConfigBeanFactory.create(config, classOf[Config])
  }
}
