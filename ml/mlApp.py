
from __future__ import print_function

from pyspark.sql import SparkSession

if __name__ == "__main__":
    spark = SparkSession \
        .builder \
        .appName("search-flight-ml") \
        .getOrCreate()

    spark.stop()
