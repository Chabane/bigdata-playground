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
  var departureDate: String = _

  @BeanProperty
  var arrivalDate: String = _

  @BeanProperty
  var passengerNumber: Int = _

  @BeanProperty
  var cabinClass: String = _
}
