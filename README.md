# Bigdata Boilerplate

## Motivation
The aim is to create a disposable Hadoop/HBase/Spark/ML stack where you can test your jobs locally or to submit them to the Yarn resource manager. We are using Docker to build the environment and Docker-Compose to provision it with the required components (Next step using Kubernetes). Along with the infrastructure, We are check that it works with 4 projects that just probes everything is working as expected. The boilerplate is based on a sample search flight web application.

Keywords : Docker, Kubernetes, Apache Spark SQL/Streaming/MLib, Scala, Python, Apache Kafka, Apache Hbase, Apache Avro, (Apache NiFi, Kylo next step), MongoDB, NodeJS (graphql, kafka-node, mongoose, avsc) Angular, Apollo-GraphQL

## Prod mode
```
docker network create vnet
cd docker
docker-compose -f mongo.yml -f zookeeper.yml -f kafka.yml -f hadoop-hbase.yml up -d
docker-compose up -d
```
## Dev mode 
```
docker network create vnet
cd batch && sbt clean package assembly
cd ..
cd streaming && sbt clean package assembly
cd ..
cd ml && sbt clean package assembly
cd ..
cd docker
docker-compose -f mongo.yml -f zookeeper.yml -f kafka.yml -f hadoop-hbase.yml up -d
docker-compose -f dev/webapp.yml up -d
docker-compose -f dev/batch.yml up -d
docker-compose -f dev/streaming.yml up -d
docker-compose -f dev/ml.yml up -d
```

## Interactions
<img src='https://image.ibb.co/jsJcLR/search_flight_v2.png'/>

## Contributing
`Pull requests` are welcome.

## Support
Please raise tickets for issues and improvements at https://github.com/Chabane/bigdata-boilerplate/issues

## License
This example is released under version 2.0 of the [Apache License](LICENSE).

