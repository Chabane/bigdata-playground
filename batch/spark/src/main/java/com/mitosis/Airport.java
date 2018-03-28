package com.mitosis;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Airport {
	private String airportId;
	private String city;
	private String country;
	private String name;
	private List<String> destinations;
}
