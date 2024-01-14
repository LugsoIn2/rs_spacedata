// SpaceDataController.scala
package SpaceData.controller
import SpaceData.model.{StarlinkSat, Launch, Rocket, SpaceEntity}
import SpaceData.controller.{active, inactive, all}
import SpaceData.util.spacexApiClient._
import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import akka.pattern.ask
import scala.concurrent.Future
import javax.xml.crypto.Data
import akka.util.Timeout
import scala.concurrent.ExecutionContextExecutor
import akka.actor.ActorRef

import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json._

import java.util.Properties
import scala.collection.JavaConverters._
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import scala.concurrent.duration._
import java.time.Duration
import org.apache.kafka.clients.consumer.ConsumerRecord



class SpaceDataControllerConsumer() {
  val launcheslist = SpaceDataLaunchController.launches(allLaunches)
  //var deserializedEntities: List[SpaceEntity] = List.empty
  var rocketslistAll: List[SpaceEntity] = List.empty
  val rocketslistAllFuture: Future[Unit] = Future {
      consumeFromKafka("rockets-all")
    }
  var rocketslisActive: List[SpaceEntity] = List.empty
  val rocketslisActiveFuture: Future[Unit] = Future {
      consumeFromKafka("rockets-active")
    }
  var rocketslisInactive: List[SpaceEntity] = List.empty
  val rocketslisInactiveFuture: Future[Unit] = Future {
      consumeFromKafka("rockets-inactive")
    }
  var starlinksatlistAll: List[SpaceEntity] = List.empty
  // val starlinksatlistAllFuture: Future[Unit] = Future {
  //     consumeFromKafka("starlinksats-all")
  //   }
    var starlinksatlistActive: List[SpaceEntity] = List.empty
  // val starlinksatlistActiveFuture: Future[Unit] = Future {
  //     consumeFromKafka("starlinksats-active")
  //   }
  var starlinksatlistInactive: List[SpaceEntity] = List.empty
  // val starlinksatlistInactiveFuture: Future[Unit] = Future {
  //     consumeFromKafka("starlinksats-inactive")
  //   }


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
          //println(s"Received message: ${record.value()}")
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
        // Hier haben Sie die deserialisierte Liste von SpaceEntity-Objekten
        spaceEntities.foreach { spaceEntity =>
          //println(s"Deserialized SpaceEntity: $spaceEntity")
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


  def getSpaceEntitiesList(slct: String, entity: String): List[SpaceEntity] = {
    val selector = stringToSelecorSpaceEntity(slct)
    val EntityList = entity match {
      case "starlinksat" => getRocketList(selector)
      case "rocket" => getRocketList(selector)
      case _ => throw new IllegalArgumentException(s"Unsupported entity type: $entity")
    }
    EntityList
  }

  def getRocketList(selector: SelectorSpaceEntity): List[SpaceEntity] = {
    val result: List[SpaceEntity] = selector match {
      case `all` =>
        rocketslistAll
      case `active` =>
        rocketslisActive
      case `inactive` =>
        rocketslisInactive
    }
    result
  }

  def getStarlinkList(selector: SelectorSpaceEntity): List[SpaceEntity] = {
    val result: List[SpaceEntity] = selector match {
      case `all` =>
        starlinksatlistAll
      case `active` =>
        starlinksatlistActive
      case `inactive` =>
        starlinksatlistInactive
    }
    result
  }


  def getSpaceEntitiyDetails(id: String, entity: String): Option[SpaceEntity] = {
    val starlinksatlist = getSpaceEntitiesList("all", entity: String)
    val foundEntitiy: Option[SpaceEntity] = findStarlinkSatById(starlinksatlist,id)
    foundEntitiy match {
      case Some(entry) =>
        Some(entry)
      case None =>
        None
    }
  }

  def findStarlinkSatById(entity: List[SpaceEntity], targetId: String): Option[SpaceEntity] = {
    entity.find(_.id == targetId)
  }


  def getLauchesList(slct: String): List[Launch] = {
    val selector = stringToSelecorLaunch(slct)
    selector match {
        case `allLaunches` => {
          launcheslist
        } case `succeeded` => {
          //TODO
          launcheslist
      } case `failed` => {
          //TODO
          launcheslist
      }
    }
  }

  def getLaunchDetails(id: String): Option[Launch] = {
    val foundLaunch: Option[Launch] = findLaunchById(launcheslist,id)
    foundLaunch match {
      case Some(launch) =>
        Some(launch)
      case None =>
        None
    }
  }

  def findLaunchById(lauches: List[Launch], targetId: String): Option[Launch] = {
    lauches.find(_.id == targetId)
  }

  def getDashboardValues(): (List[(String, Int)], List[(String, Int)], List[(String, Int)]) = {
    val dashbStarlinkVals: List[(String, Int)] =
      List(
        ("all", getSpaceEntitiesList("all", "starlinksat").size),
        ("active", getSpaceEntitiesList("active", "starlinksat").size),
        ("inactive", getSpaceEntitiesList("inactive", "starlinksat").size)
      )
    val dashbRocketsVals: List[(String, Int)] =
      List(
        ("all", getSpaceEntitiesList("all", "rocket").size),
        ("active", getSpaceEntitiesList("active", "rocket").size),
        ("inactive", getSpaceEntitiesList("inactive", "rocket").size)
      )
    val dashbLaunchVals: List[(String, Int)] =
      List(
        ("allLaunches", launcheslist.size),
        ("succeeded", launcheslist.size),
        ("failed", launcheslist.size)
      )
    (dashbStarlinkVals, dashbLaunchVals, dashbRocketsVals)
  }

  def stringToSelecorSpaceEntity(slct: String): SelectorSpaceEntity = {
      //val selector: Selector
      slct.toLowerCase match {
      case "all" => all: SelectorSpaceEntity
      case "active" => active: SelectorSpaceEntity
      case "inactive" => inactive: SelectorSpaceEntity
      case _ => throw new IllegalArgumentException("Ungültiger SelectorSpaceEntity")
    }
  }

  def stringToSelecorLaunch(slct: String): SelectorLaunch = {
      //val selector: Selector
      slct match {
      case "allLaunches" => allLaunches: SelectorLaunch
      case "succeeded" => succeeded: SelectorLaunch
      case "failed" => failed: SelectorLaunch
      case _ => throw new IllegalArgumentException("Ungültiger SelectorLaunch")
    }
  }

}