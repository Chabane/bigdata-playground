package com.mitosis.spout;

import com.typesafe.config.ConfigFactory;
import org.apache.storm.kafka.spout.*;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff.TimeInterval;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class KafkaSpoutTopologyMainNamedTopics {

    public Config getConfig() {
        Config config = new Config();
        config.setDebug(true);
        return config;
    }

    public StormTopology getTopologyKafkaSpout(KafkaSpoutConfig<String, String> spoutConfig) {

        final TopologyBuilder tp = new TopologyBuilder();
        tp.setSpout("kafka_spout", new KafkaSpout<>(spoutConfig), 1);
        tp.setBolt("kafka_bolt", new KafkaSpoutTestBolt())
                .shuffleGrouping("kafka_spout", "mitosis_stream");
        return tp.createTopology();
    }

    public KafkaSpoutConfig<String, String> getKafkaSpoutConfig(String bootstrapServers) {
        // topic names which will be read
        com.typesafe.config.Config config = ConfigFactory.parseResources("app.conf");
        String topic = config.getString("producer.topic");

        ByTopicRecordTranslator<String, String> trans = new ByTopicRecordTranslator<>(
                (r) -> new Values(r.topic(), r.partition(), r.offset(), r.key(), r.value()),
                new Fields("topic", "partition", "offset", "key", "value"), "mitosis_stream");

        return KafkaSpoutConfig.builder(bootstrapServers, new String[]{topic})
            .setProp(ConsumerConfig.GROUP_ID_CONFIG, "mitosis")
            .setRetry(getRetryService())
            .setRecordTranslator(trans)
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