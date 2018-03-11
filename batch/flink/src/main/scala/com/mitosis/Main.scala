package com.mitosis

import com.mitosis.config.ConfigurationFactory
import org.apache.log4j.Logger

import org.apache.flink.api.scala._

object Main {

  private[this] lazy val logger = Logger.getLogger(getClass)

  private[this] val config: com.mitosis.config.objects.Config = ConfigurationFactory.load()

  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
  }
}
