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
import org.apache.log4j.{Level, Logger}
import org.apache.kafka.common.serialization.StringDeserializer
// import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

import java.time.Duration
import scala.collection.JavaConverters._
import org.apache.log4j.LogManager


class SpaceDataConsumer() {
  val kafkaBroker = "localhost:9092"
  Logger.getLogger("org").setLevel(Level.WARN)
  // Logger.getLogger("akka").setLevel(Level.WARN)
  // Logger.getLogger("play").setLevel(Level.WARN)
  // Logger.getLogger("play").setLevel(Level.WARN)
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

  // def consumeFromKafkaWithSpark2(topicName: String): Unit = {
  //   val sparkConf = new SparkConf().setAppName("KafkaSparkIntegration").setMaster("local[*]")
  //   val sc = new SparkContext(sparkConf)
  //   val ssc = new StreamingContext(sc, Seconds(5))

  //   val kafkaBroker = "your_kafka_broker"
  //   // val topicName = "your_kafka_topic"

  //   val kafkaParams = Map(
  //     "bootstrap.servers" -> kafkaBroker,
  //     "key.deserializer" -> classOf[StringDeserializer].getName,
  //     "value.deserializer" -> classOf[StringDeserializer].getName,
  //     "group.id" -> "space-data-group",
  //     "auto.offset.reset" -> "earliest",
  //     "enable.auto.commit" -> "false"
  //   )

  //   val topics = Set(topicName)

  //   val kafkaStream = KafkaUtils.createDirectStream[String, String](
  //     ssc,
  //     LocationStrategies.PreferConsistent,
  //     ConsumerStrategies.Subscribe[String, String](topics, kafkaParams)
  //   )
    
  //   kafkaStream.foreachRDD { rdd =>
  //     // Hier kannst du deine bestehende Logik für die Verarbeitung der Daten einfügen
  //     rdd.foreach(record => print(record))
  //   }

  //   ssc.start()
  //   ssc.awaitTermination()
  // }


    def consumeFromKafkaWithSpark(topicName: String) {
    // Create a Spark session

    val spark = SparkSession.builder()
      .appName("SpaceDataSparkConsumer")
      .master("local[*]") // Use a local Spark cluster for testing
      // .config("spark.master", "local")
      // .config("spark.jars.packages", "com.fasterxml.jackson.module:jackson-module-scala_2.13:2.13.4")
      .getOrCreate()

    import spark.implicits._

    // Define the schema for the JSON data
    // val schema = StructType(
    //   Seq(
    //     StructField("name", StringType),
    //     StructField("id", StringType),
    //     StructField("active", BooleanType)
    //     // Add more fields based on your JSON structure
    //   )
    // )

    // Read data from Kafka topic using Spark Structured Streaming
    val kafkaStreamDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBroker)
      .option("subscribe", topicName)
      .load()
      .selectExpr("CAST(value AS STRING)") // Assuming the JSON data is in the 'value' field


      // kafkaStreamDF.writeStream.start()

    // val kafkaStreamDF = spark
    //   .readStream
    //   .format("kafka")
    //   .option("kafka.bootstrap.servers", kafkaBroker)
    //   .option("subscribe", topicName)
    //   .load()
    // kafkaStreamDF.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
    //   .as[(String, String)]

    // df.show()
    print("MOTHERFUCKER")
    // Parse JSON data using schema
    val parsedDF = kafkaStreamDF
      // .select(from_json($"value", schema).as("data"))
      // .select("data.*")

    // Perform any additional transformations as needed
    val transformedDF = parsedDF
      // .filter("age > 25")
      // .select("name", "age")

    // Write the results to the console (you can modify this to write to another sink)
    print("FUUUUUUUUUUUUUUUUU")
    val query = transformedDF.writeStream
      .outputMode("append")
      .format("console")
      .start()

    // // Await termination of the streaming query
    query.awaitTermination()

    // Stop the Spark session
    spark.stop()
  }

  private def processRecord2(recordValue: String, updateFunction: List[SpaceEntity] => Unit): Unit = {
    // Füge hier deine Logik zur Verarbeitung eines Kafka-Records ein
    // Du kannst `updateFunction` aufrufen und die `recordValue` übergeben
    // um deine bestehende Logik beizubehalten
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

  // private val futures: Map[String, Future[Unit]] = topicMappings.map {
  //   case (topic, updateFunction) => topic -> consumeFromKafka(topic, updateFunction)
  // }
}