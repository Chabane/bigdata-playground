package com.mitosis;

import java.text.MessageFormat;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.mongodb.spark.MongoSpark;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Application {

	public static void main(String[] args) {

		Config config = ConfigFactory.parseResources("app.conf");
		String mongodbUri = MessageFormat.format("mongodb://{0}:{1}/{2}.{3}", 
				config.getString("batch.db.host"),
				config.getString("batch.db.port"),
				config.getString("batch.db.database"), 
				config.getString("batch.db.collection"));
		
		SparkSession sparkSession = SparkSession
				.builder()
				.master("local[*]")
				.appName("search-flight-batch-spark")
				.config("spark.mongodb.output.uri", mongodbUri)
				.getOrCreate();

		// read parquet
		Dataset<Row> airportsDF = sparkSession.read().parquet(args[0]);
		// save airports
		MongoSpark.save(airportsDF.write().option("collection", "airports").mode("overwrite"));

		sparkSession.stop();
	}

}