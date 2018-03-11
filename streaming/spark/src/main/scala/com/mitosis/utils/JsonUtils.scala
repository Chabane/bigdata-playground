package com.mitosis.utils

import com.fasterxml.jackson.databind.ObjectMapper

object JsonUtils {

  private val OBJECT_MAPPER: ObjectMapper = new ObjectMapper()

  /**
   * Returns object as JSON encoded string
   *
   * @param input the bean instance
   * @return Returns single line JSON string
   */
  def serialize(input: AnyRef): String =
    OBJECT_MAPPER.writeValueAsString(input)

  /**
   * Returns JSON encoded string as real object
   *
   * @param input the encoded JSON string
   * @return Returns PoJo
   */
  def deserialize[T](input: String, tClass: Class[T]): T =
    OBJECT_MAPPER.readValue(input, tClass)
}
