package SpaceData.controller.kafka

import SpaceData.model.SpaceEntity
import SpaceData.util.spacexApiClient._
import play.api.libs.json._
import java.util.Properties
import scala.collection.JavaConverters._
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer, ConsumerRecord}
import java.time.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

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
    props.put("group.id", "space-data-group")
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

  // def consumeFromKafkaWithSpark(topicName: String): Unit = {
  //   // Create a Spark session

  //   val spark = SparkSession.builder()
  //     .appName("SpaceDataSparkConsumer")
  //     .master("local[*]") // Use a local Spark cluster for testing
  //     .getOrCreate()

  //   import spark.implicits._
  //   val playJsonSchemaRocket = StructType(Array(
  //     StructField("entityType", StringType, true),
  //     StructField("name", StringType, true),
  //     StructField("id", StringType, true),
  //     StructField("active", BooleanType, true)
  //   ))

  //   // Read data from Kafka topic using Spark Structured Streaming
  //   val kafkaStreamDF = spark.readStream
  //     .format("kafka")
  //     .option("kafka.bootstrap.servers", kafkaBroker)
  //     .option("subscribe", topicName)
  //     .load()
  //     // .selectExpr("CAST(value AS STRING)") // Assuming the JSON data is in the 'value' field
  //   val valuesDF = kafkaStreamDF.selectExpr("CAST(value AS STRING)")

  //   val explodedDF = valuesDF.select(from_json($"value", ArrayType(playJsonSchemaRocket)).as("data")).select(explode($"data").as("data"))
  //   val parsedDF = explodedDF.select("data.*")
  //   val rocketDF = parsedDF.as[Rocket]
    
  //   // val transformedDF = parsedDF


  //   // Write the results to the console (you can modify this to write to another sink)
  //   print("FUUUUUUUUUUUUUUUUU")
  //   val query = parsedDF.writeStream
  //     .queryName("RocketProcessing")
  //     .outputMode("append")
  //     // .format("console")
  //     .format("memory")
  //     .start()

  //   print(rocketDF.toString())
  //   // // Await termination of the streaming query
  //   query.awaitTermination()
  //   // parsedDF.show(false)

  //   // Stop the Spark session
  //   spark.stop()
  // }


  def consumeFromKafkaWithSpark(topicName: String): Unit = {
    // Create a Spark session
    val spark = SparkSession.builder()
      .appName("SpaceDataSparkConsumer")
      .master("local[*]") // Use a local Spark cluster for testing
      .getOrCreate()

    import spark.implicits._
    val rocketSchema = StructType(Array(
      StructField("entityType", StringType, true),
      StructField("name", StringType, true),
      StructField("id", StringType, true),
      StructField("active", BooleanType, true)
    ))

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
      StructField("decayed", IntegerType, true)
    ))

    val topicSchemas = Map(
      "rockets-all" -> rocketSchema,
      "rockets-active" -> rocketSchema,
      "rockets-inactive" -> rocketSchema,
      "starlinksats-all" -> starlinkSchema,
      "starlinksats-active" -> starlinkSchema,
      "starlinksats-inactive" -> starlinkSchema,
    )

    import org.apache.spark.sql.functions._
    // Get the schema and query name for the current topic
    val jsonSchema = topicSchemas(topicName)
    // val queryName = queryNames(topicName)

    // Read data from Kafka topic using Spark Structured Streaming
    val kafkaStreamDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBroker)
      .option("subscribe", topicName)
      .load()
    val valuesDF = kafkaStreamDF.selectExpr("CAST(value AS STRING)")
    
    val explodedDF = valuesDF.select(from_json($"value", ArrayType(jsonSchema)).as("data")).select(explode($"data").as("data"))
    val parsedDF = explodedDF.select("data.*")

    val tDF = topicName match {
      case "rockets-all" | "rockets-active" | "rockets-inactive" => parsedDF.as[Rocket]
      case "starlinksats-all" | "starlinksats-active" | "starlinksats-inactive" => parsedDF.as[StarlinkSat]
    }

    // tDF.collect().foreach(_ => print("FFFFFFUUUUUUUSSSSSSSS"))
    // val tDF = parsedDF.as[Rocket]
    // val dataList = tDF.collect()
    // updateFunction(dataList)
    val array = tDF.collect()
    array.foreach(println)

    // array.foreach(println)

    // Write the results to the console (you can modify this to write to another sink)
    val query = tDF.writeStream
      .queryName(topicName)
      .outputMode("append")
      .format("console")
      // .format("memory")
      .start()

    
    

    // Collect the data into a list and call the update function
    // val dataList = tDF.collect().toList
    // dataList.foreach(println)
  

    query.awaitTermination()
    // Stop the Spark session
    spark.stop()
  }



//consumeFromKafkaWithSparkGeneric[Rocket]("topicName", playJsonSchemaRocket)
//consumeFromKafkaWithSparkGeneric[StarlinkSat]("topicName", playJsonSchemaStarlinkSat)

  // def consumeFromKafkaWithSparkGeneric(topicName: String, updateFunction: List[SpaceEntity] => Unit): Future[Unit] = Future {
  //   // Create a Spark session
  //   val spark = SparkSession.builder()
  //     .appName("SpaceDataSparkConsumer")
  //     .master("local[*]") // Use a local Spark cluster for testing
  //     .getOrCreate()

  //   import spark.implicits._
  //   val rocketSchema = StructType(Array(
  //     StructField("entityType", StringType, true),
  //     StructField("name", StringType, true),
  //     StructField("id", StringType, true),
  //     StructField("active", BooleanType, true)
  //   ))

  //   val starlinkSchema = StructType(Array(
  //     StructField("entityType", StringType, true),
  //     StructField("name", StringType, true),
  //     StructField("id", StringType, true),
  //     //StructField("active", BooleanType, true)
  //     StructField("launchDate", StringType, true),
  //     StructField("period", DoubleType, true),
  //     StructField("height", DoubleType, true),
  //     StructField("latitude", DoubleType, true),
  //     StructField("longitude", DoubleType, true),
  //     StructField("earthRevolutions", IntegerType, true),
  //     StructField("decayed", IntegerType, true)
  //   ))

  //   val topicSchemas = Map(
  //     "rockets-all" -> rocketSchema,
  //     "rockets-active" -> rocketSchema,
  //     "rockets-inactive" -> rocketSchema,
  //     "starlinksats-all" -> starlinkSchema,
  //     "starlinksats-active" -> starlinkSchema,
  //     "starlinksats-inactive" -> starlinkSchema,
  //   )

  //   import org.apache.spark.sql.functions._
  //   // Get the schema and query name for the current topic
  //   val jsonSchema = topicSchemas(topicName)
  //   // val queryName = queryNames(topicName)

  //   // Read data from Kafka topic using Spark Structured Streaming
  //   val kafkaStreamDF = spark.readStream
  //     .format("kafka")
  //     .option("kafka.bootstrap.servers", kafkaBroker)
  //     .option("subscribe", topicName)
  //     .load()
  //   val valuesDF = kafkaStreamDF.selectExpr("CAST(value AS STRING)")
    
  //   val explodedDF = valuesDF.select(from_json($"value", ArrayType(jsonSchema)).as("data")).select(explode($"data").as("data"))
  //   val parsedDF = explodedDF.select("data.*")

  //   val tDF = topicName match {
  //     case "rockets-all" | "rockets-active" | "rockets-inactive" => parsedDF.as[Rocket]
  //     case "starlinksats-all" | "starlinksats-active" | "starlinksats-inactive" => parsedDF.as[StarlinkSat]
  //   }

  //   // val tDF = parsedDF.as[Rocket]

  //   // Collect the data into a list and call the update function
  //   val dataList = tDF.collect().toList
  //   dataList.foreach(println)
  //   // updateFunction(dataList)

  //   // Write the results to the console (you can modify this to write to another sink)
  //   val query = parsedDF.writeStream
  //     .queryName(topicName)
  //     .outputMode("append")
  //     .format("console")
  //     // .format("memory")
  //     .start()

  //   query.awaitTermination()
  //   // Stop the Spark session
  //   spark.stop()
  // }


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

  // private val futures: Map[String, Future[Unit]] = topicMappings.map {
  //   case (topic, updateFunction) => topic -> consumeFromKafka(topic, updateFunction)
  // }

  // private val futures: Map[String, Future[Unit]] = topicMappings.map {
  //   case (topic, updateFunction) => topic -> consumeFromKafkaWithSparkGeneric(topic, updateFunction)
  //       //updateFunction()
  // }

}


// consumeFromKafkaWithSparkGeneric[Rocket]("topicName", playJsonSchemaRocket)
// consumeFromKafkaWithSparkGeneric[StarlinkSat]("topicName", playJsonSchemaStarlinkSat)