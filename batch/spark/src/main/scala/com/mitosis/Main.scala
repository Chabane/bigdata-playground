package com.mitosis

import com.mitosis.config.ConfigurationFactory
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import com.mongodb.spark._

object Main {

  private[this] lazy val logger = Logger.getLogger(getClass)

  private[this] val config: com.mitosis.config.objects.Config = ConfigurationFactory.load()

  def main(args: Array[String]): Unit = {

    val sparkSession = SparkSession.builder
      .appName("search-flight-batch-spark")
      .config("spark.mongodb.output.uri", "mongodb://%s/%s.%s".format(
        config.batch.db.host,
        config.batch.db.database,
        config.batch.db.collection))
      .getOrCreate()

    val airportDF = sparkSession.read.parquet(args(0))
    MongoSpark.save(airportDF.write.option("collection", "airports").mode("overwrite"))

    sparkSession.stop()
  }
}
