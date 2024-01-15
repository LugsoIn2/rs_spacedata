// SpaceDataController.scala
package SpaceData.controller.kafka
import SpaceData.model.{StarlinkSat, Launch, Rocket, SpaceEntity}
import SpaceData.controller.{active, inactive, all}
import SpaceData.util.spacexApiClient._
import SpaceData.controller.HttpClientActor
import SpaceData.controller.GetSpaceEntities
import SpaceData.controller.GetCurrentState
import SpaceData.controller.SelectorSpaceEntity
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

import io.circe._
import io.circe.parser._

import play.api.libs.json.{Json, Writes}


import org.json4s._
import org.json4s.native.Serialization._




import java.util.Properties
import scala.collection.JavaConverters._
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer

class SpaceDataProducer() {
  // Actor System
  implicit val httpActorSystem: ActorSystem = ActorSystem("HttpActorSystem")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = httpActorSystem.dispatcher
  
  // create Actors
  val httpClientActorStarlinkSats = httpActorSystem.actorOf(Props(new HttpClientActor))
  val httpClientActorRockets = httpActorSystem.actorOf(Props(new HttpClientActor))

  def produceEntityToKafka() = {
    // println("producerLoop")
    // get data from API
    httpClientActorStarlinkSats ! GetSpaceEntities("/starlink")
    httpClientActorRockets ! GetSpaceEntities("/rockets")

    implicit val timeout: Timeout = Timeout(10.seconds)

    // StarlinkSats
    val futureStarlinkSatsAll: Future[List[SpaceEntity]] = (httpClientActorStarlinkSats ? GetCurrentState)
      .mapTo[List[SpaceEntity]]
      .recover { case _ => Nil }
    produceEntities(Await.result(futureStarlinkSatsAll, 10.seconds), "starlinksats-all")

    val starlinkSatsActive = Await.result(getAndFilterEntites(true, httpClientActorStarlinkSats, "starlinksat"), 10.seconds)
    produceEntities(starlinkSatsActive, "starlinksats-active")

    val starlinkSatsInactive = Await.result(getAndFilterEntites(false, httpClientActorStarlinkSats, "starlinksat"), 10.seconds)
    produceEntities(starlinkSatsInactive, "starlinksats-inactive")

    // Rockets
    val futureRocketsAll: Future[List[SpaceEntity]] = (httpClientActorRockets ? GetCurrentState)
      .mapTo[List[SpaceEntity]]
      .recover { case _ => Nil }
    produceEntities(Await.result(futureRocketsAll, 10.seconds), "rockets-all")

    val rocketsActive = Await.result(getAndFilterEntites(true, httpClientActorRockets, "rocket"), 10.seconds)
    produceEntities(rocketsActive, "rockets-active")

    val rocketsInactive = Await.result(getAndFilterEntites(false, httpClientActorRockets, "rocket"), 10.seconds)
    produceEntities(rocketsInactive, "rockets-inactive")
  }

  def getAndFilterEntites(isActive: Boolean, httpClientActor: ActorRef, entityType: String): Future[List[SpaceEntity]] = {
    implicit val timeout: Timeout = Timeout(10.seconds)
    Source.single(())
      .mapAsync(1)(_ => httpClientActor ? GetCurrentState)
      .map { data =>
        val instances = data.asInstanceOf[List[SpaceEntity]]
        entityType match {
          case "starlinksat" =>
            if (isActive) instances.collect { case sat: StarlinkSat if sat.active => sat }
            else instances.collect { case sat: StarlinkSat if !sat.active => sat }

          case "rocket" =>
            if (isActive) instances.collect { case rocket: Rocket if rocket.active => rocket }
            else instances.collect { case rocket: Rocket if !rocket.active => rocket }

          case _ => Nil // Handle other types or provide a default case
        }
      }
      .runWith(Sink.seq)
      .map(_.flatten.toList)
  }

  def produceEntities(entities: List[SpaceEntity], topicName: String): Unit = {
    //println("produceEntities")
    // Kafka Configuration
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)

    // Print JSON data before sending to Kafka
    implicit val formats: DefaultFormats.type = DefaultFormats
    var entitiesList: List[String] = List.empty
    entities.foreach { entity =>
      entitiesList = entitiesList :+ write(entity)
    }
    val message: String = entitiesList.mkString("[", ",", "]")

    val record = new ProducerRecord[String, String](topicName, message)
    producer.send(record)

    producer.close()
  }

  def stringToSelecorSpaceEntity(slct: String): SelectorSpaceEntity = {
      slct.toLowerCase match {
      case "all" => all: SelectorSpaceEntity
      case "active" => active: SelectorSpaceEntity
      case "inactive" => inactive: SelectorSpaceEntity
      case _ => throw new IllegalArgumentException("Ungültiger SelectorSpaceEntity")
    }
  }

}