FROM hseeberger/scala-sbt
VOLUME /tmp

ENV SPARK_VERSION spark-2.2.1-bin-hadoop2.7
ENV SPARK_HOME /usr/local/spark

RUN curl http://apache.mediamirrors.org/spark/spark-2.2.1/$SPARK_VERSION.tgz -o $SPARK_VERSION.tgz; \
                 tar xzf $SPARK_VERSION.tgz -C /usr/local/;
RUN cd /usr/local && ln -s $SPARK_VERSION spark

ENV BATCH_HOME /usr/local/batch

ADD src $BATCH_HOME/src
ADD parquet $BATCH_HOME/parquet
ADD build.sbt $BATCH_HOME/
ADD project/build.properties $BATCH_HOME/project/
ADD project/plugins.sbt $BATCH_HOME/project/

WORKDIR $BATCH_HOME

RUN sbt package assembly

WORKDIR $SPARK_HOME

CMD ["bin/spark-submit", "--class", "com.mitosis.Main", "--master", "local[2]", "/usr/local/batch/target/scala-2.11/search-flight-batch-assembly-0.1.0.jar", "/usr/local/batch/parquet" ]
