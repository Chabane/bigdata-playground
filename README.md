# Bigdata Playground

## Motivation
The aim is to create a disposable Hadoop/HBase/Spark/Flink/Beam/ML stack where you can test your jobs locally or to submit them to the Yarn resource manager. We are using Docker to build the environment and Docker-Compose to provision it with the required components (Next step using Kubernetes). Along with the infrastructure, We are check that it works with 4 projects that just probes everything is working as expected. The boilerplate is based on a sample search flight web application.

Keywords : Docker, (Kubernetes soon), Apache Spark SQL/Streaming(DStream)/MLib, Apache Flink, (Kafka Streams, Apache Beam soon), Scala, Python, Apache Kafka, Apache Hbase, Apache Avro, MongoDB, NodeJS (graphql, kafka-node, mongoose, avsc), Angular, Apollo-GraphQL

## Installation
If you are on mac then, you can use package manager like `brew` to install `sbt` on your machine:

```bash
$ brew install sbt
```

For other systems, you can refer to manual instructions from `sbt` website http://www.scala-sbt.org/0.13/tutorial/Manual-Installation.html. 

```
docker network create vnet
cd batch && sbt clean package assembly
cd ..
cd streaming && sbt clean package assembly
cd ..
cd docker
docker-compose -f mongo.yml -f zookeeper.yml -f kafka.yml -f hadoop-hbase.yml -f flink.yml up -d
docker-compose -f dev/webapp.yml up -d
docker-compose -f dev/batch-spark.yml up -d
docker-compose -f dev/streaming-spark.yml up -d
docker-compose -f dev/streaming-flink.yml up -d
docker-compose -f dev/ml.yml up -d
```

## Interactions / OnGoing
<img src='https://image.ibb.co/eOuL5H/search_flight_simple_v4.png'/>

## Contributing
`Pull requests` are welcome.

## Support
Please raise tickets for issues and improvements at https://github.com/Chabane/bigdata-playground/issues

## License
This example is released under version 2.0 of the [Apache License](LICENSE).

