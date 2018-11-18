package com.mitosis;

import com.mongodb.hadoop.io.BSONWritable;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.mapred.AvroValue;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import java.io.IOException;

public class MongoReducer extends Reducer<Text, AvroValue<GenericRecord>, NullWritable, BSONWritable> {

    private final BSONWritable bsonWritable;

    public MongoReducer() {
        super();
        bsonWritable = new BSONWritable();
    }

    @Override
    protected void reduce(Text key, Iterable<AvroValue<GenericRecord>> values, Context context) throws IOException, InterruptedException {
        for (AvroValue<GenericRecord> record : values) {
            BSONObject result = new BasicBSONObject();
            result.put("AirportID", record.datum().get("AirportID"));
            result.put("City", record.datum().get("City"));
            result.put("Country", record.datum().get("Country"));
            result.put("DST", record.datum().get("DST"));
            result.put("DBTZ", record.datum().get("DBTZ"));
            result.put("IATAFAA", record.datum().get("IATAFAA"));
            result.put("ICAO", record.datum().get("ICAO"));
            result.put("Latitude", record.datum().get("Latitude"));
            result.put("Longitude", record.datum().get("Longitude"));
            result.put("Name", record.datum().get("Name"));
            result.put("Timezone", record.datum().get("Timezone"));
            result.put("destinations", record.datum().get("destinations"));
            bsonWritable.setDoc(result);
            context.write(null, bsonWritable);
        }
    }
}
