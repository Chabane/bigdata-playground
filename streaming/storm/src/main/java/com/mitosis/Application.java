package com.mitosis;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Application {

	public static void main(String[] args) throws InterruptedException {

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

		String server = config.getStringList("producer.hosts").get(0);

	}

}