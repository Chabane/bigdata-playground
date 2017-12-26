FROM hseeberger/scala-sbt:8u151-2.12.4-1.0.4
VOLUME /tmp

ENV SPARK_VERSION spark-2.2.1-bin-hadoop2.7
ENV SPARK_HOME /usr/local/spark

RUN curl http://apache.mediamirrors.org/spark/spark-2.2.1/$SPARK_VERSION.tgz -o $SPARK_VERSION.tgz; \
                 tar xzf $SPARK_VERSION.tgz -C /usr/local/;
RUN cd /usr/local && ln -s $SPARK_VERSION spark

ENV STREAMING_HOME /usr/local/streaming

ADD src $STREAMING_HOME/src
ADD build.sbt $STREAMING_HOME/
ADD project/build.properties $STREAMING_HOME/project/
ADD project/plugins.sbt $STREAMING_HOME/project/

WORKDIR $STREAMING_HOME

RUN sbt package assembly

WORKDIR $SPARK_HOME

CMD ["bin/spark-submit", "--class", "com.mitosis.Main", "--master", "local[2]", "/usr/local/streaming/target/scala-2.11/search-flight-streaming-assembly-0.1.0.jar" ]
