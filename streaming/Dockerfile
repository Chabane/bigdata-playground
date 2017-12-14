FROM openjdk:8-jre
VOLUME /tmp

ENV SPARK_VERSION spark-2.2.0-bin-hadoop2.7

RUN curl -s https://d3kbcqa49mib13.cloudfront.net/$SPARK_VERSION.tgz | tar -xz -C /usr/local/
RUN cd /usr/local && ln -s $SPARK_VERSION spark
ENV SPARK_HOME /usr/local/spark

ADD target/scala-2.11/search-flight-streaming-assembly-0.1.0.jar build/libs/spark.jar
RUN sh -c 'touch build/libs/spark.jar'

WORKDIR $SPARK_HOME
CMD ["bin/spark-submit", "--class", "com.mitosis.Main", "--master", "local[2]", "/build/libs/spark.jar" ]
