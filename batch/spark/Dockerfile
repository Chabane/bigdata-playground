# FROM uhopper/hadoop:2.7.2
FROM hseeberger/scala-sbt:8u151-2.12.4-1.0.4
VOLUME /tmp

ENV SPARK_VERSION spark-2.4.0-bin-hadoop2.7
ENV SPARK_HOME /usr/local/spark

RUN curl https://archive.apache.org/dist/spark/spark-2.4.0/$SPARK_VERSION.tgz -o $SPARK_VERSION.tgz; \
                 tar xzf $SPARK_VERSION.tgz -C /usr/local/;
RUN cd /usr/local && ln -s $SPARK_VERSION spark

ENV PROJECT_HOME /usr/local/project

ADD src/main/scala $PROJECT_HOME/src/main/scala
ADD src/main/resources $PROJECT_HOME/src/main/resources
ADD parquet $PROJECT_HOME/parquet
ADD build.sbt $PROJECT_HOME/
ADD project/build.properties $PROJECT_HOME/project/
ADD project/plugins.sbt $PROJECT_HOME/project/

WORKDIR $PROJECT_HOME

RUN sbt package assembly

WORKDIR $SPARK_HOME

CMD ["bin/spark-submit", "--class", "com.mitosis.Main", "--master", "local[2]", "/usr/local/project/target/scala-2.11/search-flight-spark-batch-assembly-0.1.0.jar", "/usr/local/project/parquet" ]
