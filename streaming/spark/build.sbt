import sbt._
import Keys._

resolvers ++= Seq(
  "apache-snapshots" at "http://repository.apache.org/snapshots/",
  "Spark Packages Repo" at "https://dl.bintray.com/spark-packages/maven",
  "confluent" at "http://packages.confluent.io/maven/",
  "hortonworks" at "http://repo.hortonworks.com/content/repositories/releases/",
  Resolver.sonatypeRepo("public")
)

name := "search-flight-spark-streaming"
organization := "com.mitosis"
version := "0.1.0"

scalaVersion := "2.11.11"

val sparkVersion = "2.4.0"
val jacksonVersion = "2.9.8"
val typesafeVersion = "1.3.0"
val log4jVersion = "1.2.14"
val avroVersion = "4.0.0"
val hbaseConnectorVersion = "1.1.1-2.1-s_2.11"

libraryDependencies ++= Seq(
    "log4j" % "log4j" % log4jVersion,

    "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
    ("org.apache.spark" %% "spark-core" % sparkVersion % "provided").
    exclude("org.apache.spark", "spark-network-common_2.12").
    exclude("org.apache.spark", "spark-network-shuffle_2.12"),

    // avoid an ivy bug
    "org.apache.spark" %% "spark-network-common" % sparkVersion % "provided",
    "org.apache.spark" %% "spark-network-shuffle" % sparkVersion % "provided",
    ("org.apache.spark" %% "spark-streaming" % sparkVersion % "provided").
      exclude("org.apache.spark", "spark-core_2.12"),
    ("org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion).
      exclude("org.apache.spark", "spark-core_2.12"),

    "com.typesafe" % "config" % typesafeVersion,
    "com.databricks" %% "spark-avro" % avroVersion,
    "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
    "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
    "com.hortonworks" % "shc-core" % hbaseConnectorVersion,
)

unmanagedSourceDirectories in Compile := (scalaSource in Compile).value :: Nil

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
