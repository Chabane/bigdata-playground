package com.mitosis.config.objects

import scala.beans.{ BeanProperty, BooleanBeanProperty }

//remove if not needed
import scala.collection.JavaConversions._

class StreamingDbConfig {

  @BeanProperty
  var host: String = _

  @BeanProperty
  var port: Int = _

  @BeanProperty
  var table: String = _

  @BeanProperty
  var columnFamily: String = _
}
