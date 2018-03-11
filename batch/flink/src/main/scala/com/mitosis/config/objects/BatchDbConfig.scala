package com.mitosis.config.objects

import scala.beans.{ BeanProperty, BooleanBeanProperty }

//remove if not needed
import scala.collection.JavaConversions._

class BatchDbConfig {

  @BeanProperty
  var host: String = _

  @BeanProperty
  var port: Int = _

  @BeanProperty
  var database: String = _

  @BeanProperty
  var collection: String = _
}
