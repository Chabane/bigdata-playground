package com.mitosis.config.objects

import scala.beans.{ BeanProperty, BooleanBeanProperty }

//remove if not needed
import scala.collection.JavaConversions._

class Config {

  @BeanProperty
  var batch: BatchConfig = _
}
