package com.mitosis.spout;

import java.io.*;
import java.util.*;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.storm.shade.org.json.simple.JSONObject;
import org.apache.storm.shade.org.json.simple.parser.JSONParser;
import org.apache.storm.shade.org.json.simple.parser.ParseException;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.storm.utils.Utils.tuple;

public class KafkaFlightInfoBolt implements IBasicBolt {

    protected static final Logger LOG = LoggerFactory.getLogger(KafkaFlightInfoBolt.class);

    public void prepare(Map topoConf, TopologyContext context) {
    }

    public void execute(Tuple tuple, BasicOutputCollector collector) {
        LOG.info("input tuple > "+tuple.getValues());

        byte[] avroJson = (byte[]) tuple.getValues().get(4);

        try {
            Schema schema = Schema.parse(KafkaFlightInfoBolt.class.getResourceAsStream("/flight-info.schema.avsc"));
            DatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);

            Decoder decoder = DecoderFactory.get().binaryDecoder(avroJson, null);
            GenericRecord payload = reader.read(null, decoder);

            LOG.info("payload = "+payload);

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(payload.toString());
            String departingId = jsonObject.get("departingId").toString();
            String arrivingId = jsonObject.get("arrivingId").toString();
            String tripType = jsonObject.get("tripType").toString();
            String departureDate = jsonObject.get("departureDate").toString();
            String arrivalDate = jsonObject.get("arrivalDate").toString();
            Integer passengerNumber = Integer.valueOf(jsonObject.get("passengerNumber").toString());
            String cabinClass = jsonObject.get("cabinClass").toString();

            Random random = new java.util.Random();
            collector.emit(tuple(random.nextLong(), departingId, arrivingId, tripType, departureDate, arrivalDate, passengerNumber, cabinClass));
        } catch(ParseException e) {
            LOG.error("KafkaFlightInfoBolt - ParseException = "+e);
        } catch(IOException e) {
            LOG.error("KafkaFlightInfoBolt - IOException = "+e);
        }
    }

    public void cleanup() {

    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("rowKey", "departingId", "arrivingId", "tripType", "departureDate", "arrivalDate", "passengerNumber", "cabinClass"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
