FROM openjdk:8-jre
VOLUME /tmp

ENV SPARK_VERSION spark-2.4.0-bin-hadoop2.7
ENV SPARK_HOME /usr/local/spark
ENV PARQUET_HOME $SPARK_HOME/parquet

RUN curl https://archive.apache.org/dist/spark/spark-2.4.0/$SPARK_VERSION.tgz -o $SPARK_VERSION.tgz; \
         tar xzf $SPARK_VERSION.tgz -C /usr/local/;

RUN cd /usr/local && ln -s $SPARK_VERSION spark

ADD target/scala-2.11/search-flight-spark-batch-assembly-0.1.0.jar $SPARK_HOME/libs/search-flight-batch.jar
ADD parquet/ $PARQUET_HOME
RUN sh -c 'touch $SPARK_HOME/libs/search-flight-batch.jar'

WORKDIR $SPARK_HOME
CMD ["bin/spark-submit", "--class", "com.mitosis.Main", "--master", "local[2]", "./libs/search-flight-batch.jar", "./parquet" ]
