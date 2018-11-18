FROM openjdk:8-jre
VOLUME /tmp

ENV SPARK_VERSION spark-2.4.0-bin-hadoop2.7
ENV SPARK_HOME /usr/local/spark

RUN curl https://archive.apache.org/dist/spark/spark-2.4.0/$SPARK_VERSION.tgz -o $SPARK_VERSION.tgz; \
    tar xzf $SPARK_VERSION.tgz -C /usr/local/;

RUN cd /usr/local && ln -s $SPARK_VERSION spark

ADD hbase-site.xml $SPARK_HOME/conf
ADD target/scala-2.11/search-flight-spark-streaming-assembly-0.1.0.jar $SPARK_HOME/libs/search-flight-streaming.jar
RUN sh -c 'touch $SPARK_HOME/libs/search-flight-streaming.jar'

WORKDIR $SPARK_HOME

CMD ["bin/spark-submit", "--class", "com.mitosis.Main", "--master", "local[*]", "--files", "/usr/local/spark/conf/hbase-site.xml", "./libs/search-flight-streaming.jar"]
