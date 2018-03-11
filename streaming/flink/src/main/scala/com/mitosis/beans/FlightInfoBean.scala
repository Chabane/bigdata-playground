package com.mitosis.beans

import scala.beans.{ BeanProperty }

class FlightInfoBean {

  @BeanProperty
  var departingId: String = _

  @BeanProperty
  var arrivingId: String = _

  @BeanProperty
  var tripType: String = _

  @BeanProperty
  var departureDate: Long = _

  @BeanProperty
  var arrivalDate: Long = _

  @BeanProperty
  var passengerNumber: Int = _

  @BeanProperty
  var cabinClass: String = _
}
