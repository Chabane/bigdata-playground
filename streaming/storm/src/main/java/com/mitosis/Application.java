package com.mitosis;

import org.apache.storm.LocalCluster;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import com.mitosis.spout.SpoutTopologyMainNamedTopics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Application {

	public static void main(String[] args) throws Exception {
		new Application().run();
	}

	protected void run() {

		Config config = ConfigFactory.parseResources("app.conf");

		String flightInfoHbaseSchema = "{"
				+ "\"table\":{\"namespace\":\"default\", \"name\":\"flightInfo\", \"tableCoder\":\"PrimitiveType\"}"
				+ "\"rowkey\":\"key\"" + "\"columns\":{"
				+ "\"key\":{\"cf\":\"rowkey\", \"col\":\"key\", \"type\":\"string\"}"
				+ "\"departingId\":{\"cf\":\"searchFlightInfo\", \"col\":\"departingId\", \"type\":\"string\"}"
				+ "\"arrivingId\":{\"cf\":\"searchFlightInfo\", \"col\":\"arrivingId\", \"type\":\"string\"}"
				+ "\"tripType\":{\"cf\":\"searchFlightInfo\", \"col\":\"tripType\", \"type\":\"string\"}"
				+ "\"departureDate\":{\"cf\":\"searchFlightInfo\", \"col\":\"departureDate\", \"type\":\"string\"}"
				+ "\"arrivalDate\":{\"cf\":\"searchFlightInfo\", \"col\":\"arrivalDate\", \"type\":\"string\"}"
				+ "\"passengerNumber\":{\"cf\":\"searchFlightInfo\", \"col\":\"passengerNumber\", \"type\":\"integer\"}"
				+ "\"cabinClass\":{\"cf\":\"searchFlightInfo\", \"col\":\"cabinClass\", \"type\":\"string\"}" + "\"}"
				+ "\"}";

		String brokerUrl = config.getStringList("producer.hosts").get(0);

		SpoutTopologyMainNamedTopics topology = getTopology();
		org.apache.storm.Config tpConf = topology.getConfig();

		LocalCluster localCluster = new LocalCluster();

		// Consumer. Sets up a topology that reads the given Kafka spouts and logs the received messages
		localCluster.submitTopology("search-flight-storm-streaming", tpConf, topology.getTopologyKafkaSpout(topology.getKafkaSpoutConfig(brokerUrl)));

		stopWaitingForInput();
	}

	protected SpoutTopologyMainNamedTopics getTopology() {
		return new SpoutTopologyMainNamedTopics();
	}

	protected void stopWaitingForInput() {
		try {
			System.out.println("PRESS ENTER TO STOP");
			new BufferedReader(new InputStreamReader(System.in)).readLine();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}