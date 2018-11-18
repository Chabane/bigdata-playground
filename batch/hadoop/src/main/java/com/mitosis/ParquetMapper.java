package com.mitosis;

import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.mapred.AvroValue;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class ParquetMapper extends Mapper<LongWritable, GenericRecord, Text, AvroValue<GenericRecord>> {

    // Reuse output objects.
    private final Text outputKey = new Text();
    private final AvroValue<GenericRecord> outputValue = new AvroValue<>();
    /**
     * Just creates a simple key using two fields and passes the rest of the value directly through.
     * Do some more processing here.
     *
     * @param key     the mapper's key -- not used
     * @param value   the Avro representation of this Parquet record
     * @param context the mapper's context
     */
    @Override
    protected void map(LongWritable key, GenericRecord value, Context context) throws IOException, InterruptedException {
        outputKey.set(value.get("AirportID").toString());
        JSONObject jsonObject = new JSONObject(value.toString());

        GenericRecord datum = new GenericData.Record(value.getSchema());
        for (String keyJson : jsonObject.keySet()) {
            if("destinations".equals(keyJson)) {
                JSONArray jsonArray = (JSONArray) jsonObject.get(keyJson);
                datum.put(keyJson, jsonArray.toList());
            } else {
                datum.put(keyJson, jsonObject.get(keyJson));
            }
        }
        outputValue.datum(datum);
        context.write(outputKey,  outputValue);
    }
}
