package com.mitosis

import java.io.IOException
import java.sql.Timestamp
import java.util.Properties

import com.mitosis.config.ConfigurationFactory
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.Logger
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.sql.SparkSession

object Main {
  
  private[this] lazy val logger = Logger.getLogger(getClass)

  private[this] val config = ConfigurationFactory.load()

  def main(args: Array[String]): Unit = {
    val sparkSession = SparkSession.builder
        .appName("search-flight-streaming")
        .getOrCreate()
    
    val streamingContext = new StreamingContext(sparkSession.sparkContext, Seconds(config.streaming.window))

    val servers = config.producer.hosts.toArray.mkString(",")

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> servers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "auto.offset.reset" -> "latest",
      "group.id" -> "mitosis",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    // topic names which will be read
    val topics = Array(config.producer.topic)
    
    val stream = KafkaUtils.createDirectStream(
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    // create streaming context and submit streaming jobs
    streamingContext.start()

    // wait to killing signals etc.
    streamingContext.awaitTermination()
  }
}