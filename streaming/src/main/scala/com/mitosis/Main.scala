package com.mitosis

import com.mitosis.config.ConfigurationFactory
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.sql.SparkSession
import org.apache.kafka.common.serialization.{StringDeserializer, ByteArrayDeserializer}
import org.apache.avro.io.DatumReader
import org.apache.avro.io.Decoder
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.DecoderFactory

import org.apache.log4j.{Logger}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import org.apache.avro.SchemaBuilder

object Main {

  private[this] lazy val logger = Logger.getLogger(getClass)

  private[this] val config = ConfigurationFactory.load()

  val flightInfoSchema = SchemaBuilder
    .record("flightInfo")
    .fields
    .name("departing").`type`().stringType().noDefault()
    .name("arriving").`type`().stringType().noDefault()
    .name("tripType").`type`().enumeration("TripType").symbols("ONE_WAY", "ROUND_TRIP").noDefault()
    .name("departingDate").`type`().longType().noDefault()
    .name("arrivingDate").`type`().longType().noDefault()
    .name("passengerNumber").`type`().intType().noDefault()
    .name("cabinClass").`type`().enumeration("CabinClass").symbols("ECONOMY", "PRENIUM", "BUSINESS").noDefault()
    .endRecord

  def main(args: Array[String]): Unit = {
    val sparkSession = SparkSession.builder
        .appName("search-flight-streaming")
        .getOrCreate()

    val streamingContext = new StreamingContext(sparkSession.sparkContext, Seconds(config.streaming.window))

      val servers = config.producer.hosts.toArray.mkString(",")

        val kafkaParams = Map[String, Object](
          "bootstrap.servers" -> servers,
          "key.deserializer" -> classOf[StringDeserializer],
          "value.deserializer" -> classOf[ByteArrayDeserializer],
          "auto.offset.reset" -> "latest",
          "group.id" -> "mitosis",
          "enable.auto.commit" -> (false: java.lang.Boolean)
        )

        // topic names which will be read
        val topics = Array(config.producer.topic)

        val stream = KafkaUtils.createDirectStream(
          streamingContext,
          PreferConsistent,
          Subscribe[String, Array[Byte]](topics, kafkaParams)
        )

        stream.foreachRDD(rdd => {
          if (!rdd.isEmpty()) {
            rdd.foreach(record => {
              val reader: DatumReader[GenericRecord] = new SpecificDatumReader[GenericRecord](flightInfoSchema)
              val decoder: Decoder = DecoderFactory.get().binaryDecoder(record.value, null)
              val userData: GenericRecord = reader.read(null, decoder)
              println(userData)
            })
            println("Records to save")
          } else {
            println("No records to save")
          }
        })

    // create streaming context and submit streaming jobs
    streamingContext.start()

    // wait to killing signals etc.
    streamingContext.awaitTermination()
  }

}
