package SpaceData.controller.spark

import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.log4j.{Level, Logger, LogManager}
import SpaceData.model.StarlinkSat
import org.apache.spark.sql.streaming.Trigger
import scala.collection.mutable.ListBuffer


class SpaceDataSparkConsumer() {
    val kafkaBroker = "localhost:9092"
    Logger.getLogger("org").setLevel(Level.WARN)
    LogManager.getRootLogger.setLevel(Level.WARN)
    // Set the logging level for Kafka logger to a specific level
    Logger.getLogger("org.apache.kafka").setLevel(Level.WARN)

  def consumeFromKafkaWithSpark(topicName: String): List[StarlinkSat] = {
    val starlinksatlistSpeeds = ListBuffer[StarlinkSat]()
    val starlinkSchema = StructType(Array(
      StructField("entityType", StringType, true),
      StructField("name", StringType, true),
      StructField("id", StringType, true),
      StructField("active", BooleanType, true),
      StructField("launchDate", StringType, true),
      StructField("period", DoubleType, true),
      StructField("height", DoubleType, true),
      StructField("latitude", DoubleType, true),
      StructField("longitude", DoubleType, true),
      StructField("earthRevolutions", IntegerType, true),
      StructField("decayed", IntegerType, true),
      StructField("speed", DoubleType, true)
    ))

    val spark = SparkSession.builder()
      .appName("SpaceDataSparkConsumer")
      .master("local[*]")
      .getOrCreate()

    val kafkaStreamDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBroker)
      .option("kafka.group.id", "space-data-group-spark")
      .option("subscribe", topicName)
      .option("startingOffsets", "earliest")
      .load()
    
    val valuesDF = kafkaStreamDF.selectExpr("CAST(value AS STRING)")
    import spark.implicits._
    val explodedDF = valuesDF.select(from_json($"value", ArrayType(starlinkSchema)).as("data")).select(explode($"data").as("data"))
    val parsedDF = explodedDF.select("data.*")
    val yearToFilter = "2020" 
    val filteredDF = parsedDF.filter(year(col("launchDate")) === yearToFilter).limit(100)

    val query = filteredDF.writeStream
      .queryName(topicName)
      // .outputMode("update")
      .outputMode("append")
      // .format("foreach")
      .trigger(Trigger.Once()) 
      .foreachBatch { (batchDF: DataFrame, batchId: Long) =>
        val periodColumn = batchDF("period")
        val heightColumn = batchDF("height")
        val speedDF = batchDF.withColumn("speed", ((heightColumn + 6000) * 2 * 3.14)/periodColumn)
        // speedDF.show()
        starlinksatlistSpeeds ++= speedDF.as[StarlinkSat].collect()
        // println("Liste: " + starlinksatlistSpeeds)
        println()
      }
      .start()

    query.awaitTermination()
    // Stop the Spark session
    spark.stop()
    starlinksatlistSpeeds.toList
  }
}