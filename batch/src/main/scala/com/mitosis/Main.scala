package com.mitosis

import com.mitosis.config.ConfigurationFactory
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession

object Main {

  private[this] lazy val logger = Logger.getLogger(getClass)

  private[this] val config = ConfigurationFactory.load()

  def main(args: Array[String]): Unit = {

    val sparkSession = SparkSession.builder
      .appName("search-flight-batch")
      .getOrCreate()

    val parquetFileDF = sparkSession.read.parquet(args(0))

    parquetFileDF.createOrReplaceTempView("airportParquet")
    val airportDF = sparkSession.sql("SELECT * FROM airportParquet")
    airportDF.show()
  }
}