package me.cchandurkar

import me.cchandurkar.utils.HttpStream
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.{SQLContext, SparkSession}

object HttpStreamApp {

  def main(args: Array[String]): Unit = {

    // Create Spark Session
    val spark = SparkSession
      .builder()
      .appName(getClass.getSimpleName)
      .master("local[*]")
      .getOrCreate()

    // Port and streaming trigger interval
    val PORT = 9999
    val INTERVAL = "5 seconds"

    // Create HTTP Server and start streaming
    implicit val sqlContext: SQLContext = spark.sqlContext
    val query = new HttpStream( PORT ).toDF
      .writeStream
      .trigger(Trigger.ProcessingTime(INTERVAL))
      .foreachBatch((batch, batchId) => {
        println(s"Processing batch: $batchId")
        batch.show(false)
      })
      .start()

    // Wait for it...
    query.awaitTermination()

  }

}
