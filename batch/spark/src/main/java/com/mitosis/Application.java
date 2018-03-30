package com.mitosis;

import java.text.MessageFormat;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
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
		Dataset<Row> parquetFileDF = sparkSession.read().parquet(args[0]);
		
		// encode parquet to aiport dataset
		Encoder<Airport> airportEncoder = Encoders.bean(Airport.class);
		Dataset<Airport> airportsDS = parquetFileDF.as(airportEncoder);
		
		// save airports
		MongoSpark.save(airportsDS.write().option("collection", "airports").mode("overwrite"));

		sparkSession.stop();
	}

}