package SpaceData.controller.kafka

import SpaceData.model.SpaceEntity
import play.api.libs.json._
import java.util.Properties
import scala.collection.JavaConverters._
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer, ConsumerRecord}
import java.time.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import org.apache.log4j.{Level, Logger, LogManager}



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

    consumeRecords(consumer, updateFunction)
  }

  def consumeRecords(consumer: KafkaConsumer[String, String], updateFunction: List[SpaceEntity] => Unit): Unit = {
    val records = consumer.poll(Duration.ofMillis(100))
    // Convert the Java records to a Scala collection (e.g., List)
    // val recordList: List[ConsumerRecord[String, String]] = records.iterator().asScala.toList
    val recordList: List[ConsumerRecord[String, String]] = records.asScala.toList

    // Use map to process each record
    /*val processedRecords: List[ProcessedRecordType] =*/
    recordList.map(record => processRecord(record, updateFunction))

    consumeRecords(consumer, updateFunction)

    // Close the consumer when done
    consumer.close()
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
