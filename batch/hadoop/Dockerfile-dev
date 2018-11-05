FROM openjdk:8-jre

ENV HADOOP_HOME /usr/local/hadoop

ADD target/search-flight-hadoop-batch-0.1.0-jar-with-dependencies.jar $HADOOP_HOME/libs/search-flight-batch.jar
ADD parquet/ $HADOOP_HOME/parquet/

WORKDIR $HADOOP_HOME
CMD ["java", "-jar", "./libs/search-flight-batch.jar" ]
