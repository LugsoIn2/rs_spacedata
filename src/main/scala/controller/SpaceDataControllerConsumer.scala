// SpaceDataController.scala
package SpaceData.controller
import SpaceData.model.{StarlinkSat, Launch, Rocket, SpaceEntity}
import SpaceData.util.spacexApiClient._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
import java.util.Properties
import scala.collection.JavaConverters._
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer, ConsumerRecord}
import java.time.Duration


class SpaceDataControllerConsumer() {
  var rocketslistAll: List[SpaceEntity] = List.empty
  var rocketslisActive: List[SpaceEntity] = List.empty
  var rocketslisInactive: List[SpaceEntity] = List.empty
  var starlinksatlistAll: List[SpaceEntity] = List.empty
  var starlinksatlistActive: List[SpaceEntity] = List.empty
  var starlinksatlistInactive: List[SpaceEntity] = List.empty

  val rocketslistAllFuture: Future[Unit] = Future {
      consumeFromKafka("rockets-all")
    }
  val rocketslisActiveFuture: Future[Unit] = Future {
      consumeFromKafka("rockets-active")
    }
  val rocketslisInactiveFuture: Future[Unit] = Future {
      consumeFromKafka("rockets-inactive")
    }
  val starlinksatlistAllFuture: Future[Unit] = Future {
      consumeFromKafka("starlinksats-all")
    }
  val starlinksatlistActiveFuture: Future[Unit] = Future {
      consumeFromKafka("starlinksats-active")
    }
  val starlinksatlistInactiveFuture: Future[Unit] = Future {
      consumeFromKafka("starlinksats-inactive")
    }


  def consumeFromKafka(topicName: String): Unit = {
    // Kafka Configuration
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("group.id", "space-data-group") // Use a unique group id for the consumer
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

    val consumer = new KafkaConsumer[String, String](props)

    var deserializedEntities: List[SpaceEntity] = List.empty

    // Subscribe to the topic
    consumer.subscribe(List(topicName).asJava)

    try {
      //var deserializedEntities: List[SpaceEntity] = List.empty
      while (true) {
        val records = consumer.poll(Duration.ofMillis(100))
        records.forEach(record => 
        //  println(s"Received message: ${record.value()}")
         processRecord(record, topicName)
        )
      }
    } finally {
      consumer.close()
    }
  }

  def processRecord(record: ConsumerRecord[String, String],listidentifier: String): Unit= {
    //println(s"Processing record: ${record.value()}")
    val json = Json.parse(record.value())
    var deserializedEntities: List[SpaceEntity] = List.empty
    //println(s"Parsed JSON: $json")

    json.validate[List[SpaceEntity]] match {
      case JsSuccess(spaceEntities, _) =>
        // Deserialisierung
        spaceEntities.foreach { spaceEntity =>
          // println(s"Deserialized SpaceEntity: $spaceEntity")
          deserializedEntities = deserializedEntities :+ spaceEntity
        }
        updateGlobalLists(deserializedEntities, listidentifier)
      case JsError(errors) =>
        println(s"Error parsing JSON: $errors")
        deserializedEntities
    }
  }



  def updateGlobalLists(deserializedEntities: List[SpaceEntity], listidentifier: String): Unit = {
    listidentifier match {
      case "rockets-all" => 
        rocketslistAll = deserializedEntities
      case "rockets-active" => 
        rocketslisActive = deserializedEntities
      case "rockets-inactive" => 
        rocketslisInactive= deserializedEntities
      case "starlinksats-all" => 
        starlinksatlistAll = deserializedEntities
      case "starlinksats-active" => 
        starlinksatlistActive = deserializedEntities
      case "starlinksats-inactive" => 
        starlinksatlistInactive = deserializedEntities
    }
  }

}