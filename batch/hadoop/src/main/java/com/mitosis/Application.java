package com.mitosis;

import java.text.MessageFormat;

import com.mongodb.hadoop.MongoOutputFormat;
import com.mongodb.hadoop.io.BSONWritable;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.avro.Schema;

import org.apache.avro.mapred.AvroValue;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.parquet.avro.AvroParquetInputFormat;

public class Application extends Configured implements Tool {

	public static void main(String[] args) throws Exception {


		Config config = ConfigFactory.parseResources("app.conf");
		String mongodbUri = MessageFormat.format("mongodb://{0}:{1}/{2}.{3}",
				config.getString("batch.db.host"),
				config.getString("batch.db.port"),
				config.getString("batch.db.database"),
				config.getString("batch.db.collection"));
		Configuration conf = new Configuration();
		conf.set("mongo.output.uri", mongodbUri);

		int res = ToolRunner.run(conf, new Application(), args);
		System.exit(res);
	}

	/**
	 * @param args the arguments, which consist of the input file or directory and the output directory
	 * @return the job return status, 0 for success, 1 for failure
	 * @throws Exception
	 */
	@Override
	public int run(String[] args) throws Exception {
		JobConf conf = new JobConf(getConf());
		Job job = Job.getInstance(conf);
		job.setJarByClass(Application.class);

		Schema avroSchema = Schema.parse(ParquetMapper.class.getResourceAsStream("/airport.schema.avsc"));

		// Mapper
		job.setMapperClass(ParquetMapper.class);
		// Input
		job.setInputFormatClass(AvroParquetInputFormat.class);
		AvroParquetInputFormat.addInputPath(job, new Path("parquet/airports.parquet"));
		AvroParquetInputFormat.setAvroReadSchema(job, avroSchema);

		// Intermediate Output
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(AvroValue.class);
		AvroJob.setMapOutputValueSchema(job, avroSchema);

		// Reducer
		job.setReducerClass(MongoReducer.class);
		// Output
		job.setOutputFormatClass(MongoOutputFormat.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(BSONWritable.class);

		// Execute job and return status
		return job.waitForCompletion(true) ? 0 : 1;
	}

}