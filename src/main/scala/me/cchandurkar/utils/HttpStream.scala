package me.cchandurkar.utils

import java.net.InetSocketAddress
import java.sql.Timestamp

import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import org.apache.spark.sql.execution.streaming.MemoryStream
import org.apache.spark.sql.{DataFrame, Encoder, Encoders, SQLContext}

import scala.io.Source

/**
 * Streaming data read from local server will have this schema
 * @param value - The payload POSTed to http endpoint.
 * @param timestamp - Timestamp of when it was put on a stream.
 */
case class HttpData(value: String, timestamp: Timestamp)

/**
 * Stream reads data from an HTTP endpoint. It hosts a temp
 * server and listens to the POST requests made on http endpoint.
 * It puts data on `MemoryStream` which is read by spark pipeline.
 * For local use only.
 *
 * @param port - Http port to host.
 * @param baseUrl - An endpoint base path.
 */
class HttpStream(port: Int = 8866, baseUrl: String = "/") {

  /**
   * Starts an HTTP server and initializes memory stream.
   * As requests are made to given http endpoint, it puts data on memory stream.
   * Returns a streaming DF created off of memory stream.
   *
   * @param sqlContext
   * @return
   */
  def toDF(implicit sqlContext: SQLContext): DataFrame = {

    // Create a memory Stream
    implicit val enc: Encoder[HttpData] = Encoders.product[HttpData]
    val stream = MemoryStream[HttpData]

    // Create server
    val server = HttpServer.create(new InetSocketAddress(port), 0)
    server.setExecutor(null)
    server.createContext(
      baseUrl,
      new HttpHandler {
        override def handle(httpExchange: HttpExchange): Unit = {
          val payload = Source.fromInputStream(httpExchange.getRequestBody).mkString
          val timestamp = new java.sql.Timestamp(System.currentTimeMillis())
          val offset = stream.addData(HttpData(payload, timestamp))
          val response = s"""{ "success": true, "timestamp": "$timestamp", "offset": $offset }"""
          sendResponse(httpExchange, status = 200, response)
        }
      }
    )

    // Start server and return streaming DF
    server.start()
    stream.toDF()

  }

  /**
   * Send back a response with provided status code and response text
   * @param he
   * @param status
   * @param response
   */
  def sendResponse(he: HttpExchange, status: Int, response: String): Unit = {
    he.getResponseHeaders.set("Content-Type", "application/json");
    he.sendResponseHeaders(status, response.length)
    val os = he.getResponseBody
    os.write(response.getBytes)
    os.close()
  }

}
