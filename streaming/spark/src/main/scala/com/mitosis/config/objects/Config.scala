package com.mitosis.config.objects

import scala.beans.{ BeanProperty, BooleanBeanProperty }

//remove if not needed
import scala.collection.JavaConversions._

class Config {

  @BeanProperty
  var producer: ProducerConfig = _

  @BeanProperty
  var streaming: StreamingConfig = _
}
