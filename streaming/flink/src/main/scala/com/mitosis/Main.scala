package com.mitosis

import java.io.IOException
import java.util.Properties

import com.mitosis.beans.FlightInfoBean
import com.mitosis.utils.JsonUtils
import com.mitosis.config.ConfigurationFactory
import org.apache.log4j.Logger

import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011

object Main {

  private[this] lazy val logger = Logger.getLogger(getClass)

  private[this] val config = ConfigurationFactory.load()

  /**
    * Json decode UDF function
    *
    * @param text the encoded JSON string
    * @return Returns record bean
    */
  def jsonDecode(text: String): FlightInfoBean = {
    try {
      JsonUtils.deserialize(text, classOf[FlightInfoBean])
    } catch {
      case e: IOException =>
        logger.error(e.getMessage, e)
        null
    }
  }

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.getConfig.setRestartStrategy(RestartStrategies.fixedDelayRestart(4, 10000))
    // create a checkpoint every 5 seconds
    env.enableCheckpointing(5000)

    val servers = config.producer.hosts.toArray.mkString(",")
    val topic = config.producer.topic

    val properties = new Properties();
    properties.setProperty("bootstrap.servers", servers)
    properties.setProperty("group.id", "mitosis")

    val kafkaConsumer = new FlinkKafkaConsumer011(topic, new SimpleStringSchema, properties)
    kafkaConsumer.setStartFromLatest()

    val messageStream = env.addSource(kafkaConsumer)
    messageStream.print()

    env.execute("search-flight-flink-streaming")
  }

}
