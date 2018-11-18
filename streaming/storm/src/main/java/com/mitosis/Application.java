package com.mitosis;

import com.mitosis.spout.KafkaSpoutTopology;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.storm.LocalCluster;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;

public class Application {

	public static void main(String[] args) throws IOException {
		new Application().run();
	}

	protected void run() throws IOException {

		org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
		conf.addResource("hbase-site.xml");

		HBaseAdmin admin = new HBaseAdmin(conf);

		if (!admin.isTableAvailable("flightInfo")) {
			HTableDescriptor flightInfoTableDesc = new HTableDescriptor(Bytes.toBytes("flightInfo"));
			HColumnDescriptor searchFlightInfoColumnFamilyDesc = new HColumnDescriptor(Bytes.toBytes("searchFlightInfo"));
			flightInfoTableDesc.addFamily(searchFlightInfoColumnFamilyDesc);
			admin.createTable(flightInfoTableDesc);
		}

		Config config = ConfigFactory.parseResources("app.conf");

		String brokerUrl = config.getStringList("producer.hosts").get(0);

		KafkaSpoutTopology topology = getTopology();
		org.apache.storm.Config tpConf = topology.getConfig();

		LocalCluster localCluster = new LocalCluster();

		// Consumer. Sets up a topology that reads the given Kafka spouts and logs the received messages
		localCluster.submitTopology("search-flight-storm-streaming", tpConf, topology.getTopology(topology.getSpoutConfig(brokerUrl)));

	}

	protected KafkaSpoutTopology getTopology() {
		return new KafkaSpoutTopology();
	}
}