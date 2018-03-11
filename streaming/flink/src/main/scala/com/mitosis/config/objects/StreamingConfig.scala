package com.mitosis.config.objects

import scala.beans.{ BeanProperty, BooleanBeanProperty }

//remove if not needed
import scala.collection.JavaConversions._

class StreamingConfig {

  @BeanProperty
  var db: StreamingDbConfig = _

  @BeanProperty
  var window: Int = _
}
