package com.mitosis.config.objects

import java.util.List

import scala.beans.{ BeanProperty, BooleanBeanProperty }

//remove if not needed
import scala.collection.JavaConversions._

class ProducerConfig {

  @BeanProperty
  var hosts: List[String] = _

  @BeanProperty
  var batchSize: Int = _

  @BeanProperty
  var topic: String = _
}
