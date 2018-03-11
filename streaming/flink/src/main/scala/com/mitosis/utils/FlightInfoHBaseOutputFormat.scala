package com.mitosis.utils

import org.apache.hadoop.hbase.HBaseConfiguration

import org.apache.flink.api.common.io.OutputFormat
import org.apache.flink.configuration.Configuration

import org.apache.hadoop.hbase.{ HColumnDescriptor, HTableDescriptor, HBaseConfiguration }
import org.apache.hadoop.hbase.client.HBaseAdmin
import org.apache.hadoop.hbase.client.HTable
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes
import com.mitosis.beans.FlightInfoBean
import java.io.IOException
import org.apache.log4j.Logger

/**
 * This class implements an OutputFormat for HBase.
 */
class FlightInfoHBaseOutputFormat extends OutputFormat[String] {

  private[this] lazy val logger = Logger.getLogger(getClass)

  def jsonDecode(text: String): FlightInfoBean = {
    try {
      JsonUtils.deserialize(text, classOf[FlightInfoBean])
    } catch {
      case e:
        IOException =>
        logger.error(e.getMessage, e)
        null
    }
  }

  private var conf: org.apache.hadoop.conf.Configuration = null
  private var flightInfoTable: HTable = null

  override def configure(parameters: Configuration): Unit = {
    conf = HBaseConfiguration.create()
    conf.addResource("hbase-site.xml")

    val admin = new HBaseAdmin(conf)

    if (!admin.isTableAvailable("flightInfo")) {
      val flightInfoTableDesc = new HTableDescriptor(Bytes.toBytes("flightInfo"))
      val searchFlightInfoColumnFamilyDesc = new HColumnDescriptor(Bytes.toBytes("searchFlightInfo"))
      flightInfoTableDesc.addFamily(searchFlightInfoColumnFamilyDesc)
      admin.createTable(flightInfoTableDesc)
    }
  }

  override def open(taskNumber: Int, numTasks: Int): Unit = {
    flightInfoTable = new HTable(conf, "flightInfo")
  }

  override def writeRecord(record: String): Unit = {
    val flightInfo = jsonDecode(record)
    val random = scala.util.Random
    val rowKey = random.nextLong
    val flightInfoRow = new Put(Bytes.toBytes(rowKey))

    flightInfoRow.add(Bytes.toBytes("searchFlightInfo"), Bytes.toBytes("departingId"), Bytes.toBytes(flightInfo.departingId))
    flightInfoRow.add(Bytes.toBytes("searchFlightInfo"), Bytes.toBytes("arrivingId"), Bytes.toBytes(flightInfo.arrivingId))
    flightInfoRow.add(Bytes.toBytes("searchFlightInfo"), Bytes.toBytes("tripType"), Bytes.toBytes(flightInfo.tripType))
    flightInfoRow.add(Bytes.toBytes("searchFlightInfo"), Bytes.toBytes("departureDate"), Bytes.toBytes(flightInfo.departureDate))
    flightInfoRow.add(Bytes.toBytes("searchFlightInfo"), Bytes.toBytes("arrivalDate"), Bytes.toBytes(flightInfo.arrivalDate))
    flightInfoRow.add(Bytes.toBytes("searchFlightInfo"), Bytes.toBytes("passengerNumber"), Bytes.toBytes(flightInfo.passengerNumber))
    flightInfoRow.add(Bytes.toBytes("searchFlightInfo"), Bytes.toBytes("cabinClass"), Bytes.toBytes(flightInfo.cabinClass))

    flightInfoTable.put(flightInfoRow)
  }

  override def close(): Unit = {
    flightInfoTable.flushCommits()
    flightInfoTable.close()
  }
}
