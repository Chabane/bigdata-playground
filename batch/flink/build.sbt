import sbt._
import Keys._

resolvers in ThisBuild ++= Seq("Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
  Resolver.mavenLocal)

name := "search-flight-flink-batch"
organization := "com.mitosis"
version := "0.1.0"

mainClass in assembly := Some("com.mitosis.Main")

scalaVersion := "2.11.11"

val flinkVersion = "1.4.1"
val jacksonVersion = "2.9.8"
val typesafeVersion = "1.3.0"
val log4jVersion = "1.2.14"

val projectDependencies = Seq(
    "log4j" % "log4j" % log4jVersion,
    "org.apache.flink" %% "flink-scala" % flinkVersion % "provided",

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
