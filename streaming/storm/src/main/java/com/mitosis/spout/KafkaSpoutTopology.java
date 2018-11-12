package com.mitosis.spout;

import com.typesafe.config.ConfigFactory;
import org.apache.storm.kafka.spout.*;

import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff.TimeInterval;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

public class KafkaSpoutTopology {

    public Config getConfig() {
        Config config = new Config();
        config.setDebug(true);
        return config;
    }

    public StormTopology getTopology(KafkaSpoutConfig<String, String> spoutConfig) {

        final TopologyBuilder tp = new TopologyBuilder();
        tp.setSpout("kafka_spout", new KafkaSpout<>(spoutConfig), 1);
        tp.setBolt("kafka_bolt", new KafkaBolt())
                .shuffleGrouping("kafka_spout");
        return tp.createTopology();
    }

    public KafkaSpoutConfig<String, String> getSpoutConfig(String bootstrapServers) {
        // topic names which will be read
        com.typesafe.config.Config config = ConfigFactory.parseResources("app.conf");
        String topic = config.getString("producer.topic");

        return KafkaSpoutConfig.builder(bootstrapServers, topic)
            .setRetry(getRetryService())
            .setOffsetCommitPeriodMs(10_000)
            .setFirstPollOffsetStrategy(KafkaSpoutConfig.FirstPollOffsetStrategy.LATEST)
            .setMaxUncommittedOffsets(250)
            .build();
    }

    public KafkaSpoutRetryService getRetryService() {
        return new KafkaSpoutRetryExponentialBackoff(TimeInterval.microSeconds(500),
            TimeInterval.milliSeconds(2), Integer.MAX_VALUE, TimeInterval.seconds(10));
    }
}
