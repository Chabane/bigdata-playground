import sbt._
import Keys._

resolvers in ThisBuild ++= Seq("Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
  Resolver.mavenLocal)

name := "search-flight-flink-streaming"
organization := "com.mitosis"
version := "0.1.0"

mainClass in assembly := Some("com.mitosis.Main")

scalaVersion := "2.11.11"

val flinkVersion = "1.4.1"
val jacksonVersion = "2.6.5"
val typesafeVersion = "1.3.0"
val log4jVersion = "1.2.14"
val hbaseVersion = "1.3.1"
val hadoopVersion = "2.5.1"

val projectDependencies = Seq(
    "log4j" % "log4j" % log4jVersion,
    "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided",
    "org.apache.flink" %% "flink-hbase" % flinkVersion,
    "org.apache.flink" % "flink-avro" % flinkVersion,
    "org.apache.flink" %% "flink-connector-kafka-0.11" % flinkVersion,
    "org.apache.hbase" % "hbase-common" % hbaseVersion,
    ("org.apache.hadoop" % "hadoop-common" % hadoopVersion)
    .exclude("commons-beanutils", "commons-beanutils-core")
    .exclude("commons-beanutils", "commons-beanutils"),

    "com.typesafe" % "config" % typesafeVersion,
    "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
    "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion
)


lazy val root = (project in file(".")).
  settings(
    libraryDependencies ++= projectDependencies
  )

// make run command include the provided dependencies
run in Compile := Defaults.runTask(fullClasspath in Compile,
  mainClass in (Compile, run),
  runner in (Compile,run)
).evaluated

// exclude Scala library from assembly
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)