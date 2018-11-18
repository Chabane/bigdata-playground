package com.mitosis.spout;

import com.typesafe.config.ConfigFactory;
import org.apache.storm.hbase.bolt.HBaseBolt;
import org.apache.storm.hbase.bolt.mapper.SimpleHBaseMapper;
import org.apache.storm.kafka.spout.*;

import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff.TimeInterval;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

import java.util.HashMap;
import java.util.Map;

public class KafkaSpoutTopology {

    public Config getConfig() {
        Config config = new Config();
        config.setDebug(true);
        Map<String, Object> hbConf = new HashMap<>();
        hbConf.put("hbase.rootdir", "hdfs://namenode-1:8020/hbase");
        hbConf.put("hbase.zookeeper.quorum","zoo1");
        hbConf.put("hbase.master.port","16020");
        hbConf.put("zookeeper.znode.parent","/hbase");
        hbConf.put("hbase.zookeeper.property.clientPort","2181");
        hbConf.put("hbase.cluster.distributed","true");
        hbConf.put("hbase.rest.ssl.enabled","false");

        config.put("hbase.conf", hbConf);
        return config;
    }

    public StormTopology getTopology(KafkaSpoutConfig<String, String> spoutConfig) {

        final TopologyBuilder tp = new TopologyBuilder();
        tp.setSpout("kafka_spout", new KafkaSpout<>(spoutConfig), 1);
        KafkaFlightInfoBolt kafkaBolt = new KafkaFlightInfoBolt();

        SimpleHBaseMapper mapper = new SimpleHBaseMapper()
                .withRowKeyField("rowKey")
                .withColumnFields(new Fields("rowKey", "departingId", "arrivingId", "tripType", "departureDate", "arrivalDate", "passengerNumber", "cabinClass"))
                .withColumnFamily("searchFlightInfo");

        HBaseBolt hbaseBolt = new HBaseBolt("flightInfo", mapper)
                .withConfigKey("hbase.conf");

        tp.setBolt("kafka_bolt", kafkaBolt)
                .shuffleGrouping("kafka_spout");

        tp.setBolt("hbase_bolt", hbaseBolt, 1)
                .fieldsGrouping("kafka_bolt", new Fields("rowKey", "departingId", "arrivingId", "tripType", "departureDate", "arrivalDate", "passengerNumber", "cabinClass"));

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
            .setProp("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer")
            .setProp("value.deserializer",
                "org.apache.kafka.common.serialization.ByteArrayDeserializer")
            .build();
    }

    public KafkaSpoutRetryService getRetryService() {
        return new KafkaSpoutRetryExponentialBackoff(TimeInterval.microSeconds(500),
            TimeInterval.milliSeconds(2), Integer.MAX_VALUE, TimeInterval.seconds(10));
    }
}
