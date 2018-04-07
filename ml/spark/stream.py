from __future__ import print_function

import avro.schema
import tweepy
import os

from io import BytesIO
from avro.datafile import DataFileReader, DataFileWriter
from avro.io import DatumReader, DatumWriter, BinaryEncoder, BinaryDecoder
from avro.schema import Parse

from pyspark.sql import *
from pyspark.sql.types import *
from pyspark.sql.functions import col, explode

import json

os.environ['PYSPARK_SUBMIT_ARGS'] = '--packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.3.0'
dir_path = os.path.dirname(os.path.realpath(__file__))

def deserialize(flight_info_bytes) :
    if flight_info_bytes is not None:
        bytes_reader = BytesIO(flight_info_bytes)
        decoder = BinaryDecoder(bytes_reader)
        schema_flight_info = Parse(open(dir_path + "/flight-info.schema.avsc", "rb").read())
        reader = DatumReader(schema_flight_info)
        flight_info = reader.read(decoder)

        return json.dumps([{"id": 907955534287978496}])
    else:
        return None

def initialize() :

    spark = SparkSession \
        .builder \
        .appName("search-flight-spark-ml-stream") \
        .getOrCreate()

    search_flight_df = spark \
        .readStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "kafka:9092") \
        .option("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer") \
        .option("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer") \
        .option("subscribe", "flightInfoTopic") \
        .option("auto.offset.reset", "latest") \
        .option("group.id", "mitosis") \
        .option("enable.auto.commit", False) \
        .load()

    spark.udf.register("deserialize", deserialize)

    search_flight_ds = search_flight_df\
        .selectExpr("key", "deserialize(value) as value") \
        .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")

    search_flight_ds \
        .writeStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "kafka:9092") \
        .option("topic", "tweetsTopic") \
        .option("group.id", "mitosis") \
        .option("checkpointLocation", "/tmp/checkpoint") \
        .option("key.serializer", "org.apache.kafka.common.serialization.StringSerializer") \
        .option("value.serializer", "org.apache.kafka.common.serialization.StringSerializer") \
        .start() \
        .awaitTermination()

    spark.stop()

if __name__ == "__main__":
    initialize()
