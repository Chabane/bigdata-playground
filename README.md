# Search flight Big data app

A full example of a big data application using : Kubernetes, Apache Spark SQL/Streaming/MLib, Scala, Apache Kafka, Apache Hbase, Apache Parquet, Apache Avro, MongoDB, NodeJS (graphql, kafka-node, mongoose, avsc) Angular, Apollo-GraphQL

# prod mode
```
docker-compose up -d
```
# dev mode 
```cd batch && sbt clean package assembly
cd ..
cd streaming && sbt clean package assembly
cd ..
docker-compose -f docker-compose-dev.yml up
```
<img src='https://image.ibb.co/cgzCUm/search_flight.png'/>
