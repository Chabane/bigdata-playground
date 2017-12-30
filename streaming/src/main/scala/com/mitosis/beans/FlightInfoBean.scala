package com.mitosis.beans

import scala.beans.{BeanProperty}

class FlightInfoBean {

  @BeanProperty
  var departing: String = _
  
  @BeanProperty
  var arriving: String = _
  
  @BeanProperty
  var tripType: String = _
  
  @BeanProperty
  var departingDate: Long = _
  
  @BeanProperty
  var arrivingDate: Long = _
  
  @BeanProperty
  var passengerNumber: Short = _
  
  @BeanProperty
  var cabinClass: String = _
}