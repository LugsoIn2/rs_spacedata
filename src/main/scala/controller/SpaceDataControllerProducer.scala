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

import io.circe._
import io.circe.parser._


import java.util.Properties
import scala.collection.JavaConverters._
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer

class SpaceDataControllerProducer() {
  // Actor System
  implicit val httpActorSystem: ActorSystem = ActorSystem("HttpActorSystem")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = httpActorSystem.dispatcher
  
  // create Actors
  val httpClientActorStarlinkSats = httpActorSystem.actorOf(Props(new HttpClientActor))
  val httpClientActorRockets = httpActorSystem.actorOf(Props(new HttpClientActor))

  // get data from API
  httpClientActorStarlinkSats ! GetSpaceEntities("/starlink")
  httpClientActorRockets ! GetSpaceEntities("/rockets")

  //ar starlinksatlist = SpaceDataStarLinkController.starlink(all)
  // val starlinksatlistActive = SpaceDataStarLinkController.starlink(active)
  // val starlinksatlistInactive = SpaceDataStarLinkController.starlink(inactive)

  val launcheslist = SpaceDataLaunchController.launches(allLaunches)

  //def producerEntityList()

  def produceSpaceEntitiesList(slct: String, entity: String)/*: List[SpaceEntity]*/ = {
    val selector = stringToSelecorSpaceEntity(slct)
    implicit val timeout: Timeout = Timeout(10.seconds)
    val httpClientActor = entity match {
      case "starlinksat" => httpClientActorStarlinkSats
      case "rocket" => httpClientActorRockets
      case _ => throw new IllegalArgumentException(s"Unsupported entity type: $entity")
    }

    val result: List[SpaceEntity] = selector match {
      // case `all` =>
      //   val futureEntities: Future[List[SpaceEntity]] = (httpClientActor ? GetCurrentState)
      //     .mapTo[List[SpaceEntity]]
      //     .recover { case _ => Nil }
      //   val entities = Await.result(futureEntities, 10.seconds)
      //   val props = new Properties()
      //   props.put("bootstrap.servers", "localhost:9092")
      //   props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      //   props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

      //   val producer = new KafkaProducer[String, String](props)

      //   val topic = "test-topic"
      //   val message = "Hello, Kafka!"

      //   val record = new ProducerRecord[String, String](topic, message)

      //   producer.send(record)
      //   producer.close()
      //   entities


      case `all` =>
        val futureEntities: Future[List[SpaceEntity]] = (httpClientActor ? GetCurrentState)
          .mapTo[List[SpaceEntity]]
          .recover { case _ => Nil }
        val entities = Await.result(futureEntities, 10.seconds)
        produceEntities(entities, "starlinksats-all")
        entities

      
      case `active` =>
        val entities = Await.result(getAndFilterEntites(true, httpClientActor, entity), 10.seconds)
        produceEntities(entities, "starlinksats-aactive")
        entities

      case `inactive` =>
        val entities = Await.result(getAndFilterEntites(false, httpClientActor, entity), 10.seconds)
        produceEntities(entities, "starlinksats-inactive")
        entities
    }

    result
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
    // Kafka Configuration
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)

    // Print JSON data before sending to Kafka
    var entitiesList: List[String] = List.empty
    entities.foreach { entity =>
      entitiesList = entitiesList :+ entity.toString()
    }
    val message: String = entitiesList.mkString("[\"", "\",\"", "\"]")
    val record = new ProducerRecord[String, String](topicName, message)
    producer.send(record)

    producer.close()
  }


  def testconsumer(){
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "your-consumer-group-id")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")

    val consumer = new KafkaConsumer[String, String](props)
    val topic = "test-topic"
    consumer.subscribe(List(topic).asJava)

    while (true) {
      import scala.concurrent.duration._
      import java.time.Duration

      val records = consumer.poll(Duration.ofMillis(100))
      records.forEach(record => println(s"Received message: ${record.value()}"))
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