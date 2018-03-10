package com.mitosis

import java.io.IOException
import java.util.Properties

import com.mitosis.beans.FlightInfoBean
import com.mitosis.utils.JsonUtils
import com.mitosis.config.ConfigurationFactory
import org.apache.log4j.Logger

import org.apache.avro.io.DatumReader
import org.apache.avro.io.Decoder
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.DecoderFactory
import org.apache.avro.SchemaBuilder

import scala.io.Source

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.util.serialization.DeserializationSchema

import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011

import scala.io.Source

object Main {

 private[this] lazy val logger = Logger.getLogger(getClass)

 private[this] val config = ConfigurationFactory.load()


 val flightInfoHbaseSchema = s"""{
                |"table":{"namespace":"default", "name":"flightInfo", "tableCoder":"PrimitiveType"},
                |"rowkey":"key",
                |"columns":{
                |"key":{"cf":"rowkey", "col":"key", "type":"string"},
                |"departingId":{"cf":"searchFlightInfo", "col":"departingId", "type":"string"},
                |"arrivingId":{"cf":"searchFlightInfo", "col":"arrivingId", "type":"string"},
                |"tripType":{"cf":"searchFlightInfo", "col":"tripType", "type":"string"},
                |"departureDate":{"cf":"searchFlightInfo", "col":"departureDate", "type":"bigint"},
                |"arrivalDate":{"cf":"searchFlightInfo", "col":"arrivalDate", "type":"bigint"},
                |"passengerNumber":{"cf":"searchFlightInfo", "col":"passengerNumber", "type":"integer"},
                |"cabinClass":{"cf":"searchFlightInfo", "col":"cabinClass", "type":"string"}
                |}
                |}""".stripMargin

  def jsonDecode(text: String): FlightInfoBean = {
    try {
    JsonUtils.deserialize(text, classOf[FlightInfoBean])
    } catch {
    case e:
      IOException =>
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

    val kafkaConsumer = new FlinkKafkaConsumer011(topic, FlightInfoAvroSchema, properties)
    kafkaConsumer.setStartFromLatest()

    val messageStream = env.addSource(kafkaConsumer)
    messageStream.map(record => {
      println(jsonDecode(record))
      record
    })

    messageStream.print()
    env.execute("search-flight-flink-streaming")
  }

  object FlightInfoAvroSchema extends DeserializationSchema[String] {

    import org.apache.flink.api.common.typeinfo.TypeInformation
    import org.apache.flink.api.java.typeutils.TypeExtractor

    override def isEndOfStream(t: String): Boolean = false

    override def deserialize(message: Array[Byte]): String = {
      val flightInfoAvroSchema: Schema = new Parser().parse(Source.fromURL(getClass.getResource("/flight-info.schema.avsc")).mkString)

      val reader: DatumReader[GenericRecord] = new SpecificDatumReader[GenericRecord](flightInfoAvroSchema)
      val decoder: Decoder = DecoderFactory.get().binaryDecoder(message, null)

      val flightInfoJson: GenericRecord = reader.read(null, decoder)
      flightInfoJson.toString
    }
    override def getProducedType: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])
  }
}