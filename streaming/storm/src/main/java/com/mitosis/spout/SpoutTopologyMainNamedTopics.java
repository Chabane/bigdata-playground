package com.mitosis.spout;

import com.typesafe.config.ConfigFactory;
import org.apache.storm.kafka.spout.*;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff.TimeInterval;
import org.apache.storm.topology.TopologyBuilder;

/**
 * This example sets up 3 topologies to put data in Kafka via the KafkaBolt,
 * and shows how to set up a topology that reads from some Kafka topics using the KafkaSpout.
 */
public class SpoutTopologyMainNamedTopics {

    public Config getConfig() {
        Config config = new Config();
        config.setDebug(true);
        return config;
    }

    public StormTopology getTopologyKafkaSpout(KafkaSpoutConfig<String, String> spoutConfig) {

        final TopologyBuilder tp = new TopologyBuilder();
        tp.setSpout("kafka_spout", new KafkaSpout<>(spoutConfig), 1);
        return tp.createTopology();
    }

    public KafkaSpoutConfig<String, String> getKafkaSpoutConfig(String bootstrapServers) {
        // topic names which will be read
        com.typesafe.config.Config config = ConfigFactory.parseResources("app.conf");
        String topic = config.getString("producer.topic");

        return KafkaSpoutConfig.builder(bootstrapServers, new String[]{topic})
            .setProp(ConsumerConfig.GROUP_ID_CONFIG, "mitosis")
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