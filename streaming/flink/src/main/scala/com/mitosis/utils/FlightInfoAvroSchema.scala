package com.mitosis.utils

import scala.io.Source

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser

import org.apache.avro.io.DatumReader
import org.apache.avro.io.Decoder
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.DecoderFactory
import org.apache.avro.SchemaBuilder

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.util.serialization.DeserializationSchema

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
