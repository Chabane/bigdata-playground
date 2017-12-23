import sbt._
import Keys._

resolvers ++= Seq(
  "apache-snapshots" at "http://repository.apache.org/snapshots/",
  "Spark Packages Repo" at "https://dl.bintray.com/spark-packages/maven"
)

name := "search-flight-streaming"
organization := "com.mitosis"
version := "0.1.0"

scalaVersion := "2.11.11"

val sparkVersion = "2.2.1"
val typesafeVersion = "1.3.0"
val log4jVersion = "1.2.14"
val avroVersion = "4.0.0"
val hbaseVersion = "2.0.0-alpha4"

libraryDependencies ++= Seq(
    "log4j" % "log4j" % log4jVersion,

    "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
    ("org.apache.spark" %% "spark-core" % sparkVersion % "provided").
    exclude("org.apache.spark", "spark-network-common_2.11").
    exclude("org.apache.spark", "spark-network-shuffle_2.11"),

    // avoid an ivy bug
    "org.apache.spark" %% "spark-network-common" % sparkVersion % "provided",
    "org.apache.spark" %% "spark-network-shuffle" % sparkVersion % "provided",
    ("org.apache.spark" %% "spark-streaming" % sparkVersion % "provided").
      exclude("org.apache.spark", "spark-core_2.11"),
    ("org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion).
      exclude("org.apache.spark", "spark-core_2.11"),

    "com.typesafe" % "config" % typesafeVersion,
    "com.databricks" %% "spark-avro" % avroVersion
    // "org.apache.hbase" %% "hbase-spark" % hbaseVersion
)

assemblyMergeStrategy in assembly := {
  case PathList("org", "apache", "spark", "unused", "UnusedStubClass.class") => MergeStrategy.first
  case x => (assemblyMergeStrategy in assembly).value(x)
}