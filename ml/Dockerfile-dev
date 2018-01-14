FROM python:2.7-alpine

RUN apk add --no-cache curl bash alpine-sdk openjdk8

ENV JAVA8_HOME /usr/lib/jvm/default-jvm
ENV JAVA_HOME $JAVA8_HOME

ENV SPARK_VERSION spark-2.2.1-bin-hadoop2.7
ENV SPARK_HOME /usr/local/spark

RUN curl http://apache.mediamirrors.org/spark/spark-2.2.1/$SPARK_VERSION.tgz -o $SPARK_VERSION.tgz; \
                 tar xzf $SPARK_VERSION.tgz -C /usr/local/;
RUN cd /usr/local && ln -s $SPARK_VERSION spark

ADD mlApp.py $SPARK_HOME/libs/mlApp.py

WORKDIR $SPARK_HOME
CMD ["bin/spark-submit", "--master", "local[*]", "libs/mlApp.py"]
