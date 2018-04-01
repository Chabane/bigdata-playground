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

os.environ['PYSPARK_SUBMIT_ARGS'] = '--packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.3.0'
dir_path = os.path.dirname(os.path.realpath(__file__))

def deserialize(flight_info_bytes) :
    if flight_info_bytes is not None:
        bytes_reader = BytesIO(flight_info_bytes)
        decoder = BinaryDecoder(bytes_reader)
        schema_flight_info = Parse(open(dir_path + "/flight-info.schema.avsc", "rb").read())
        reader = DatumReader(schema_flight_info)
        flight_info = reader.read(decoder)
        return [{"id": "1"}, {"id": "2"}]
    else:
        return None

def serialize(tweets) :
    if tweets is not None:
        schema_tweet = avro.schema.Parse(open(dir_path + "/tweet.schema.avsc", "rb").read())

        writer = DatumWriter()
        bytes_writer = BytesIO()
        encoder = BinaryEncoder(bytes_writer)
        writer.write_array(schema_tweet, tweets, encoder)
        tweets_bytes = bytes_writer.getvalue()
        return tweets_bytes
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
        .option("kafka.bootstrap.servers", "kafka.vnet:9092") \
        .option("subscribe", "flightInfoTopic") \
        .option("auto.offset.reset", "latest") \
        .option("group.id", "mitosis") \
        .load()

    flight_info_schema_data_type = StructType([
        StructField("departingId", StringType(), False),
        StructField("arrivingId", StringType(), False),
        StructField("tripType", StringType(), False),
        StructField("departureDate", StringType(), False),
        StructField("arrivalDate", StringType(), False),
        StructField("passengerNumber", IntegerType(), False),
        StructField("cabinClass", StringType(), False),
    ])

    tweet_schema_data_type = StructType([
        StructField("id", StringType(), False)
    ])

    spark.udf.register("deserialize", deserialize, flight_info_schema_data_type)
    spark.udf.register("serialize", serialize)

    search_flight_ds = search_flight_df\
        .selectExpr("key", "deserialize(value) as tweets")\
        .selectExpr("key", "serialize(tweets) as value")\
        .selectExpr("CAST(key AS STRING)", "CAST(value AS BINARY)")

    search_flight_ds \
        .writeStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "kafka.vnet:9092") \
        .option("topic", "tweetsTopic") \
        .option("group.id", "mitosis") \
        .option("checkpointLocation", "/tmp/checkpoint") \
        .start() \
        .awaitTermination()

    spark.stop()

if __name__ == "__main__":
    initialize()
