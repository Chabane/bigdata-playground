# BigData Spark Kafka full example

A full example of a big data application using : Kubernetes, Apache Spark SQL/Streaming/MLib, Scala, Apache Kafka, Apache Hbase, Apache Parquet, Apache Avro, Apache NiFi, Kylo (datalake), MongoDB, NodeJS (graphql, kafka-node, mongoose, avsc) Angular, Apollo-GraphQL

# prod mode
```
docker-compose up -d
```
# dev mode 
```
cd batch && sbt clean package assembly
cd ..
cd streaming && sbt clean package assembly
cd ..
docker-compose -f docker-compose-dev.yml up
```
<img src='https://image.ibb.co/jsJcLR/search_flight_v2.png'/>

## Contributing
`Pull requests` are welcome;

## Support
Please raise tickets for issues and improvements at https://github.com/Chabane/bigdata-spark-kafka-full-example/issues

## License
This example is released under version 2.0 of the [Apache License](LICENSE).

