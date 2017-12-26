FROM hseeberger/scala-sbt:8u151-2.12.4-1.0.4
VOLUME /tmp

ENV SPARK_VERSION spark-2.2.1-bin-hadoop2.7
ENV SPARK_HOME /usr/local/spark

RUN curl http://apache.mediamirrors.org/spark/spark-2.2.1/$SPARK_VERSION.tgz -o $SPARK_VERSION.tgz; \
                 tar xzf $SPARK_VERSION.tgz -C /usr/local/;
RUN cd /usr/local && ln -s $SPARK_VERSION spark

ENV ML_HOME /usr/local/ml

ADD src $ML_HOME/src
ADD build.sbt $ML_HOME/
ADD project/build.properties $ML_HOME/project/
ADD project/plugins.sbt $ML_HOME/project/

WORKDIR $ML_HOME

RUN sbt package assembly

WORKDIR $SPARK_HOME

CMD ["bin/spark-submit", "--class", "com.mitosis.Main", "--master", "local[2]", "/usr/local/ml/target/scala-2.11/search-flight-ml-assembly-0.1.0.jar" ]
