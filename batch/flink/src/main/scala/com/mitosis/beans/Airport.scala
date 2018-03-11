package com.mitosis.beans

import scala.beans.{ BeanProperty, BooleanBeanProperty }

//remove if not needed
import scala.collection.JavaConversions._

class Airport {

  @BeanProperty
  var code: String = _

  @BeanProperty
  var city: String = _

  @BeanProperty
  var airport: String = _
}
