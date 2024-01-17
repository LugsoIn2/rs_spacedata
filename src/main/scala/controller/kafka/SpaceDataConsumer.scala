package SpaceData.controller.kafka

import SpaceData.model.SpaceEntity
// import SpaceData.util.spacexApiClient._
import play.api.libs.json._
import java.util.Properties
import scala.collection.JavaConverters._
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer, ConsumerRecord}
import java.time.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import org.apache.spark.sql._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.{SparkConf, SparkContext}
// import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010
import org.apache.spark.sql.kafka010
import org.apache.spark.sql.Column
import org.apache.log4j.{Level, Logger}
import org.apache.kafka.common.serialization.StringDeserializer
// import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

import java.time.Duration
import scala.collection.JavaConverters._
import org.apache.log4j.LogManager
import SpaceData.model.Rocket
import SpaceData.model.StarlinkSat
import scala.util.Success
import scala.util.Failure
import org.apache.spark.sql.Row
import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql._
import org.apache.spark.sql.streaming.Trigger
import scala.collection.mutable.ListBuffer
import org.apache.spark.sql.functions._

class SpaceDataConsumer() {
  val kafkaBroker = "localhost:9092"
  Logger.getLogger("org").setLevel(Level.WARN)
  LogManager.getRootLogger.setLevel(Level.WARN)
    // Set the logging level for Kafka logger to a specific level
  Logger.getLogger("org.apache.kafka").setLevel(Level.WARN)

  var rocketslistAll: List[SpaceEntity] = List.empty
  var rocketslisActive: List[SpaceEntity] = List.empty
  var rocketslisInactive: List[SpaceEntity] = List.empty
  var starlinksatlistAll: List[SpaceEntity] = List.empty
  var starlinksatlistActive: List[SpaceEntity] = List.empty
  var starlinksatlistInactive: List[SpaceEntity] = List.empty
  // var starlinksatlistSpeed: List[SpaceEntity] = List.empty

  private val topicMappings = Map(
    "rockets-all" -> this.updateRocketsListAll _,
    "rockets-active" -> this.updateRocketsListActive _,
    "rockets-inactive" -> this.updateRocketsListInactive _,
    "starlinksats-all" -> this.updateStarlinkSatListAll _,
    "starlinksats-active" -> this.updateStarlinkSatListActive _,
    "starlinksats-inactive" -> this.updateStarlinkSatListInactive _
  )

  private def consumeFromKafka(topicName: String, updateFunction: List[SpaceEntity] => Unit): Future[Unit] = Future {
    val props = new Properties()
    props.put("bootstrap.servers", kafkaBroker)
    props.put("group.id", "space-data-group-kafka")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

    val consumer = new KafkaConsumer[String, String](props)
    consumer.subscribe(List(topicName).asJava)

    try {
      while (true) {
        val records = consumer.poll(Duration.ofMillis(100))
        records.forEach(record => processRecord(record, updateFunction))
      }
    } finally {
      consumer.close()
    }
  }

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

  private def processRecord(record: ConsumerRecord[String, String], updateFunction: List[SpaceEntity] => Unit): Unit = {
    val json = Json.parse(record.value())

    json.validate[List[SpaceEntity]] match {
      case JsSuccess(spaceEntities, _) => updateFunction(spaceEntities)
      case JsError(errors) => println(s"Error parsing JSON: $errors")
    }
  }

  private def updateRocketsListAll(entities: List[SpaceEntity]): Unit = {
    rocketslistAll = entities
  }

  private def updateRocketsListActive(entities: List[SpaceEntity]): Unit = {
    rocketslisActive = entities
  }

  private def updateRocketsListInactive(entities: List[SpaceEntity]): Unit = {
    rocketslisInactive = entities
  }

  private def updateStarlinkSatListAll(entities: List[SpaceEntity]): Unit = {
    starlinksatlistAll = entities
  }

  private def updateStarlinkSatListActive(entities: List[SpaceEntity]): Unit = {
    starlinksatlistActive = entities
  }

  private def updateStarlinkSatListInactive(entities: List[SpaceEntity]): Unit = {
    starlinksatlistInactive = entities
  }

  private val futures: Map[String, Future[Unit]] = topicMappings.map {
    case (topic, updateFunction) => topic -> consumeFromKafka(topic, updateFunction)
  }

}
