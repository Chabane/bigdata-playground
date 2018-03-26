package com.mitosis;

import org.apache.spark.sql.SparkSession;

import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.Config;

import scala.Tuple2;

public class Application {

    public static void main(String[] args){

        Config config = ConfigFactory.parseResources("app.conf");

        SparkSession sparkSession = SparkSession
            .builder()
            .master("local[*]")
            .appName("search-flight-batch")
            .config("spark.mongodb.output.uri", "mongodb://%s/%s.%s".format(
                    config.getString("batch.db.host"),
                    config.getString("batch.db.database"),
                    config.getString("batch.db.collection")
                )
            )
            .getOrCreate();
    }

}