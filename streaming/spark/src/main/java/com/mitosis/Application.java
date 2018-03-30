package com.mitosis;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Application {

	public static void main(String[] args) throws InterruptedException {

		Config config = ConfigFactory.parseResources("app.conf");

		SparkSession sparkSession = SparkSession
				.builder()
				.master("local[*]")
				.appName("search-flight-spark-streaming")
				.config("spark.driver.allowMultipleContexts", "true") // to remove
				.getOrCreate();

		String flightInfoHbaseSchema = "{"
				+ "\"table\":{\"namespace\":\"default\", \"name\":\"flightInfo\", \"tableCoder\":\"PrimitiveType\"}"
				+ "\"rowkey\":\"key\"" + "\"columns\":{"
				+ "\"key\":{\"cf\":\"rowkey\", \"col\":\"key\", \"type\":\"string\"}"
				+ "\"departingId\":{\"cf\":\"searchFlightInfo\", \"col\":\"departingId\", \"type\":\"string\"}"
				+ "\"arrivingId\":{\"cf\":\"searchFlightInfo\", \"col\":\"arrivingId\", \"type\":\"string\"}"
				+ "\"tripType\":{\"cf\":\"searchFlightInfo\", \"col\":\"tripType\", \"type\":\"string\"}"
				+ "\"departureDate\":{\"cf\":\"searchFlightInfo\", \"col\":\"departureDate\", \"type\":\"string\"}"
				+ "\"arrivalDate\":{\"cf\":\"searchFlightInfo\", \"col\":\"arrivalDate\", \"type\":\"string\"}"
				+ "\"passengerNumber\":{\"cf\":\"searchFlightInfo\", \"col\":\"passengerNumber\", \"type\":\"integer\"}"
				+ "\"cabinClass\":{\"cf\":\"searchFlightInfo\", \"col\":\"cabinClass\", \"type\":\"string\"}" + "\"}"
				+ "\"}";

		JavaStreamingContext streamingContext = new JavaStreamingContext(new StreamingContext(sparkSession.sparkContext(), new Duration(1000)));

		String server = config.getStringList("producer.hosts").get(0);

		Map<String, Object> kafkaParams = new HashMap<>();
		kafkaParams.put("bootstrap.servers", server);
		kafkaParams.put("key.deserializer", StringDeserializer.class);
		kafkaParams.put("value.deserializer", ByteArrayDeserializer.class);
		kafkaParams.put("auto.offset.reset", "latest");
		kafkaParams.put("group.id", "mitosis");
		kafkaParams.put("enable.auto.commit", false);

		// topic names which will be read
		Collection<String> topics = Arrays.asList(config.getString("producer.topic"));

		JavaInputDStream<ConsumerRecord<String, byte[]>> stream = KafkaUtils.createDirectStream(streamingContext,
				LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, byte[]>Subscribe(topics, kafkaParams));

		stream.foreachRDD(rdd -> {
			JavaRDD<FlightInfoBean> flightInfoRdd = rdd.map(record -> {

				Schema flightInfoAvroSchema = new Parser()
						.parse(new File(Application.class.getResource("/flight-info.schema.avsc").toURI()));
				DatumReader<GenericRecord> reader = new SpecificDatumReader<GenericRecord>(flightInfoAvroSchema);
				Decoder decoder = DecoderFactory.get().binaryDecoder(record.value(), null);
				GenericRecord flightInfoJson = reader.read(null, decoder);
				ObjectMapper mapper = new ObjectMapper();

				FlightInfoBean flightInfoBean = mapper.readValue(flightInfoJson.toString(), FlightInfoBean.class);
				Random random = new java.util.Random();
				flightInfoBean.setRowKey(random.nextLong());
				return flightInfoBean;
			});

			Dataset<Row> flightInfoDF = sparkSession.createDataFrame(flightInfoRdd, FlightInfoBean.class);

			flightInfoDF.write()
					.option("HBaseTableCatalog.tableCatalog", flightInfoHbaseSchema)
					.option("HBaseTableCatalog.newTable", 8)
					.format("org.apache.spark.sql.execution.datasources.hbase")
					.save();
		});

		// create streaming context and submit streaming jobs
		streamingContext.start();

		// wait to killing signals etc.
		streamingContext.awaitTermination();
	}

}