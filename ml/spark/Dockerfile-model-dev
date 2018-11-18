FROM python:3.6.4-alpine

RUN apk add --no-cache curl bash alpine-sdk openjdk8
RUN pip install tweepy pyspark numpy

ENV JAVA8_HOME /usr/lib/jvm/default-jvm
ENV JAVA_HOME $JAVA8_HOME

ENV SPARK_VERSION spark-2.4.0-bin-hadoop2.7
ENV SPARK_HOME /usr/local/spark

RUN curl https://archive.apache.org/dist/spark/spark-2.4.0/$SPARK_VERSION.tgz -o $SPARK_VERSION.tgz; \
                 tar xzf $SPARK_VERSION.tgz -C /usr/local/;
RUN cd /usr/local && ln -s $SPARK_VERSION spark

ADD model.py $SPARK_HOME/libs/mlApp.py

WORKDIR $SPARK_HOME
RUN python3 -m py_compile libs/mlApp.py
CMD ["bin/spark-submit", "--master", "local[*]", "libs/mlApp.py"]