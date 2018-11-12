package com.mitosis.spout;

import java.util.Map;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaBolt extends BaseRichBolt {

    protected static final Logger LOG = LoggerFactory.getLogger(KafkaBolt.class);
    private OutputCollector collector;

    @Override
    public void prepare(Map topoConf, TopologyContext context, OutputCollector collector) {
        LOG.info("ehyyyyyyyyyyy", context);
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        LOG.info("input = [" + input + "]");

        collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
}
