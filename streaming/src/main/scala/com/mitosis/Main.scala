package com.mitosis

import java.io.IOException

import org.apache.spark.sql.{DataFrame, SparkSession, Row}
import org.apache.spark.sql.types.{StructField, StructType, IntegerType, LongType, StringType}
import com.mitosis.beans.FlightInfoBean
import com.mitosis.utils.JsonUtils
import com.mitosis.config.ConfigurationFactory
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.sql.SparkSession
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import org.apache.avro.io.DatumReader
import org.apache.avro.io.Decoder
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.DecoderFactory
import org.apache.log4j.Logger
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.avro.SchemaBuilder
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

  val flightInfoAvroSchema = SchemaBuilder
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

  val flightInfoHbaseSchema = s"""{
                |"table":{"namespace":"default", "name":"flightInfo", "tableCoder":"PrimitiveType"},
                |"rowkey":"key",
                |"columns":{
                |"key":{"cf":"rowkey", "col":"key", "type":"string"},
                |"departing":{"cf":"searchFlightInfo", "col":"departing", "type":"string"},
                |"arriving":{"cf":"searchFlightInfo", "col":"arriving", "type":"string"},
                |"tripType":{"cf":"searchFlightInfo", "col":"tripType", "type":"string"},
                |"departingDate":{"cf":"searchFlightInfo", "col":"departingDate", "type":"bigint"},
                |"arrivingDate":{"cf":"searchFlightInfo", "col":"arrivingDate", "type":"bigint"},
                |"passengerNumber":{"cf":"searchFlightInfo", "col":"passengerNumber", "type":"integer"},
                |"cabinClass":{"cf":"searchFlightInfo", "col":"cabinClass", "type":"string"}
                |}
                |}""".stripMargin


  val flightInfoDfSchema = new StructType()
    .add(StructField("key", StringType, true))
    .add(StructField("departing", StringType, true))
    .add(StructField("arriving", StringType, true))
    .add(StructField("tripType", StringType, true))
    .add(StructField("departingDate", LongType, true))
    .add(StructField("arrivingDate", LongType, true))
    .add(StructField("passengerNumber", IntegerType, true))
    .add(StructField("cabinClass", StringType, true))

  def main(args: Array[String]): Unit = {

    val sparkSession = SparkSession.builder
        .appName("search-flight-streaming")
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
        val flightInfoRdd = rdd.map(record => {
              println(record.value())

              val reader: DatumReader[GenericRecord] = new SpecificDatumReader[GenericRecord](flightInfoAvroSchema)
              val decoder: Decoder = DecoderFactory.get().binaryDecoder(record.value, null)
              val flightInfoJson: GenericRecord = reader.read(null, decoder)
              println(flightInfoJson.toString)
              val flightInfo = jsonDecode(flightInfoJson.toString)
              val random = scala.util.Random
              Row(
                s"${random.nextLong()}",
                flightInfo.departing,
                flightInfo.arriving,
                flightInfo.tripType,
                flightInfo.departingDate,
                flightInfo.arrivingDate,
                flightInfo.passengerNumber,
                flightInfo.cabinClass
              )
        })

        val flightInfoDF = sparkSession.createDataFrame(flightInfoRdd, flightInfoDfSchema)
        flightInfoDF.show()

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
