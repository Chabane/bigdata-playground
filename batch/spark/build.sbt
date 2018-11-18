import sbt._
import Keys._

name := "search-flight-spark-batch"
organization := "com.mitosis"
version := "0.1.0"

scalaVersion := "2.11.11"

val sparkVersion = "2.4.0"
val mongodbVersion = "2.2.1"
val typesafeVersion = "1.3.0"
val log4jVersion = "1.2.14"

libraryDependencies ++= Seq(
    "log4j" % "log4j" % log4jVersion,

    "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
    ("org.apache.spark" %% "spark-core" % sparkVersion % "provided").
    exclude("org.apache.spark", "spark-network-common_2.12").
    exclude("org.apache.spark", "spark-network-shuffle_2.12"),

    // avoid an ivy bug
    "org.apache.spark" %% "spark-network-common" % sparkVersion % "provided",
    "org.apache.spark" %% "spark-network-shuffle" % sparkVersion % "provided",

    "com.typesafe" % "config" % typesafeVersion,
    "org.mongodb.spark" %% "mongo-spark-connector" % mongodbVersion

)

unmanagedSourceDirectories in Compile := (scalaSource in Compile).value :: Nil

assemblyMergeStrategy in assembly := {
  case PathList("org", "apache", "spark", "unused", "UnusedStubClass.class") => MergeStrategy.first
  case x => (assemblyMergeStrategy in assembly).value(x)
}