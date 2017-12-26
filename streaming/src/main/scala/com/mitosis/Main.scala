package com.mitosis

import java.io.IOException

import com.mitosis.beans.FlightInfoBean
import com.mitosis.utils.JsonUtils
import com.mitosis.config.ConfigurationFactory
import it.nerdammer.spark.hbase.conversion.FieldWriter
import org.apache.hadoop.hbase.util.Bytes
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


        implicit def resultWriter: FieldWriter[FlightInfoBean] = new FieldWriter[FlightInfoBean]
        {
          val random = scala.util.Random
          override def map(flightInfo: FlightInfoBean): HBaseData =
            Seq(
              // here when you insert to HBase table you need to pass 1 extra argument, as compare to HBase table reading mapping.
              Some(Bytes.toBytes(s"${random.nextLong}")),
              Some(Bytes.toBytes(flightInfo.tripType)),
              Some(Bytes.toBytes(flightInfo.arriving)),
              Some(Bytes.toBytes(flightInfo.departing)),
              Some(Bytes.toBytes(flightInfo.departingDate)),
              Some(Bytes.toBytes(flightInfo.arrivingDate)),
              Some(Bytes.toBytes(flightInfo.passengerNumber)),
              Some(Bytes.toBytes(flightInfo.cabinClass))
            )
          override def columns = Seq(
            "tripType",
            "arriving",
            "departing",
            "departingDate",
            "arrivingDate",
            "passengerNumber",
            "cabinClass"
          )
        }

    stream.foreachRDD(rdd => {
          import it.nerdammer.spark.hbase._

          rdd.map(record => {
              val reader: DatumReader[GenericRecord] = new SpecificDatumReader[GenericRecord](flightInfoSchema)
              val decoder: Decoder = DecoderFactory.get().binaryDecoder(record.value, null)
              val flightInfoJson: GenericRecord = reader.read(null, decoder)
              val flightInfo = jsonDecode(flightInfoJson.toString)
              flightInfo
            }).toHBaseTable("flightInfo")
            .inColumnFamily("flightInfoCF")
            .save()
        })

    // create streaming context and submit streaming jobs
    streamingContext.start()

    // wait to killing signals etc.
    streamingContext.awaitTermination()
  }

}
