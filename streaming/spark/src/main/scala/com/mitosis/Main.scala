package com.mitosis

import java.io.IOException
import scala.io.Source

import org.apache.spark.sql.{ DataFrame, SparkSession, Row }
import org.apache.spark.sql.types.{ StructField, StructType, IntegerType, LongType, StringType }
import com.mitosis.beans.FlightInfoBean
import com.mitosis.utils.JsonUtils
import com.mitosis.config.ConfigurationFactory
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.sql.SparkSession
import org.apache.kafka.common.serialization.{ ByteArrayDeserializer, StringDeserializer }
import org.apache.avro.io.DatumReader
import org.apache.avro.io.Decoder
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.DecoderFactory
import org.apache.log4j.Logger
import org.apache.spark.streaming.{ Seconds, StreamingContext }

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog

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

  val flightInfoHbaseSchema = s"""{
                |"table":{"namespace":"default", "name":"flightInfo", "tableCoder":"PrimitiveType"},
                |"rowkey":"key",
                |"columns":{
                |"key":{"cf":"rowkey", "col":"key", "type":"string"},
                |"departingId":{"cf":"searchFlightInfo", "col":"departingId", "type":"string"},
                |"arrivingId":{"cf":"searchFlightInfo", "col":"arrivingId", "type":"string"},
                |"tripType":{"cf":"searchFlightInfo", "col":"tripType", "type":"string"},
                |"departureDate":{"cf":"searchFlightInfo", "col":"departureDate", "type":"string"},
                |"arrivalDate":{"cf":"searchFlightInfo", "col":"arrivalDate", "type":"string"},
                |"passengerNumber":{"cf":"searchFlightInfo", "col":"passengerNumber", "type":"integer"},
                |"cabinClass":{"cf":"searchFlightInfo", "col":"cabinClass", "type":"string"}
                |}
                |}""".stripMargin

  val flightInfoDfSchema = new StructType()
    .add(StructField("key", StringType, true))
    .add(StructField("departingId", StringType, true))
    .add(StructField("arrivingId", StringType, true))
    .add(StructField("tripType", StringType, true))
    .add(StructField("departureDate", StringType, true))
    .add(StructField("arrivalDate", StringType, true))
    .add(StructField("passengerNumber", IntegerType, true))
    .add(StructField("cabinClass", StringType, true))

  def main(args: Array[String]): Unit = {

    val sparkSession = SparkSession.builder
      .appName("search-flight-spark-streaming")
      .config("spark.hbase.host", config.streaming.db.host)
      .getOrCreate()

    val streamingContext = new StreamingContext(sparkSession.sparkContext, Seconds(config.streaming.window))

    val servers = config.producer.hosts.toArray.mkString(",")

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> servers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[ByteArrayDeserializer],
      "auto.offset.reset" -> "latest",
      "group.id" -> "mitosis",
      "enable.auto.commit" -> (false: java.lang.Boolean))

    // topic names which will be read
    val topics = Array(config.producer.topic)

    val stream = KafkaUtils.createDirectStream(
      streamingContext,
      PreferConsistent,
      Subscribe[String, Array[Byte]](topics, kafkaParams))

    stream.foreachRDD(rdd => {
      val flightInfoRdd = rdd.map(record => {

        val flightInfoAvroSchema: Schema = new Parser().parse(Source.fromURL(getClass.getResource("/flight-info.schema.avsc")).mkString)
        val reader: DatumReader[GenericRecord] = new SpecificDatumReader[GenericRecord](flightInfoAvroSchema)
        val decoder: Decoder = DecoderFactory.get().binaryDecoder(record.value, null)
        val flightInfoJson: GenericRecord = reader.read(null, decoder)
        val flightInfo = jsonDecode(flightInfoJson.toString)
        val random = scala.util.Random
        Row(
          s"${random.nextLong()}",
          flightInfo.departingId,
          flightInfo.arrivingId,
          flightInfo.tripType,
          flightInfo.departureDate,
          flightInfo.arrivalDate,
          flightInfo.passengerNumber,
          flightInfo.cabinClass)
      })

      val flightInfoDF = sparkSession.createDataFrame(flightInfoRdd, flightInfoDfSchema)

      val sc = sparkSession.sparkContext
      val sqlContext = sparkSession.sqlContext

      import sqlContext.implicits._

      flightInfoDF.write.options(
        Map(HBaseTableCatalog.tableCatalog -> flightInfoHbaseSchema, HBaseTableCatalog.newTable -> "5"))
        .format("org.apache.spark.sql.execution.datasources.hbase")
        .save()
    })

    // create streaming context and submit streaming jobs
    streamingContext.start()

    // wait to killing signals etc.
    streamingContext.awaitTermination()
  }

}
