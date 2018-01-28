import sbt._
import Keys._

resolvers ++= Seq(
  "apache-snapshots" at "http://repository.apache.org/snapshots/"
)

name := "search-flight-flink-streaming"
organization := "com.mitosis"
version := "0.1.0"

scalaVersion := "2.11.11"

val flinkVersion = "1.4.0"
val jacksonVersion = "2.6.5"
val typesafeVersion = "1.3.0"
val log4jVersion = "1.2.14"

libraryDependencies ++= Seq(
    "log4j" % "log4j" % log4jVersion,
    "org.apache.flink" %% "flink-streaming-scala" % flinkVersion,
    "org.apache.flink" %% "flink-hbase" % flinkVersion,
    "org.apache.flink" % "flink-avro" % flinkVersion,

    "com.typesafe" % "config" % typesafeVersion,
    "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
    "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
