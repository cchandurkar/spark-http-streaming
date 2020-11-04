# Spark HTTP Streaming
This project demonstrates how you can use a local HTTP server as a streaming source to debug a Structured Streaming job on local machine. The idea is to have spark app start a local HTTP server and put the ingested data on [MemoryStream](https://jaceklaskowski.gitbooks.io/spark-structured-streaming/content/spark-sql-streaming-MemoryStream.html) and use it as a streaming source.

> Note that this is for testing and running locally only. Since it uses Memory Stream underneath, it is not fault-tolerant. Refer to the [fault-tolerance semantics](https://spark.apache.org/docs/latest/structured-streaming-programming-guide.html#fault-tolerance-semantics) in structured streaming.

For more details please refer to the blog post:<br/>[Spark Streaming with HTTP REST endpoint serving JSON data](https://towardsdatascience.com/apache-spark-stream-reading-data-from-local-http-server-d37e90e70fb0)

## How to use
1. Run the `HttpStreamApp` spark application
2. `POST` sample JSON data to `http://localhost:9999`

## Demo
Watch: https://www.youtube.com/watch?v=Y9g4oj5GH5k <br/>
You will see that the spark app ingest that data in micro-batches of Structured Streaming and displays it. 
