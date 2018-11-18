FROM flink:1.6.2-hadoop28-alpine
VOLUME /tmp

ADD target/scala-2.11/search-flight-flink-streaming-assembly-0.1.0.jar $FLINK_HOME/libs/search-flight-streaming.jar
RUN sh -c 'touch $FLINK_HOME/libs/search-flight-streaming.jar'

WORKDIR $FLINK_HOME

CMD ["./bin/flink", "run", "-m", "jobmanager:6123", "-c", "com.mitosis.Main", "./libs/search-flight-streaming.jar", "--port", "9000"]
