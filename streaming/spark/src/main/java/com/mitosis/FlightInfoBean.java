package com.mitosis;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FlightInfoBean {

  private Long rowKey;
  private String departingId;
  private String arrivingId;
  private String tripType;
  private Long departureDate;
  private Long arrivalDate;
  private Integer passengerNumber;
  private String cabinClass;
}
