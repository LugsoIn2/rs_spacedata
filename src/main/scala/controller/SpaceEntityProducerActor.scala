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
import scala.util.{Failure, Success}

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
import akka.actor.ActorLogging
import akka.actor.Actor
import akka.actor.Status
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.Http
import akka.http.scaladsl.unmarshalling.Unmarshal


// Message to trigger HTTP request in actor
case class ProduceEntities(entityType: String, slct: SelectorSpaceEntity)


// Actor responsible for making HTTP requests and maintaining state
class SpaceEntityProducerActor extends Actor with ActorLogging {
  implicit val system: ActorSystem = context.system
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = context.dispatcher
  val hostUrl: String = "https://api.spacexdata.com/v4"

  def receive: Receive = {
    case ProduceEntities(entityType, slct) =>
      implicit val timeout: Timeout = Timeout(10.seconds)
      val request = HttpRequest(uri = hostUrl + "/" + entityType)
          val responseFuture = Http().singleRequest(request)

          responseFuture.onComplete {
            case Success(response) =>
              val data = Unmarshal(response.entity).to[String] //response.entity.toString()
              data.onComplete {
                case Success(body) =>
                  val spaceEntities = createSpaceEntitiesInstances(entityType, body)
                  slct match {
                    case `all` =>
                      produceEntities(spaceEntities, entityType+"-all")
                    case `inactive` => 
                      val filteredEntities: List[SpaceEntity] = filterEntites(false, spaceEntities, entityType)
                      produceEntities(filteredEntities, entityType + "-inactive")
                    case `active` => 
                      val filteredEntities: List[SpaceEntity] = filterEntites(true, spaceEntities, entityType)
                      produceEntities(filteredEntities, entityType + "-active")
                  }
                case Failure(ex) =>
                    println(s"Failed to unmarshal response body: $ex")
              }
            case Failure(ex) =>
              println(s"Request to /$entityType failed: $ex")
        }
      
  }

  def createSpaceEntitiesInstances(entityType: String, body: String): List[SpaceEntity] = {
    val dataAsList: List[Json] = SpaceData.util.spacexApiClient.Helpers.parseToList(body, "get")
    var entityList: List[SpaceEntity] = List.empty
    entityType match {
      case "starlink" =>
          dataAsList.foreach { item =>
              entityList = entityList :+ StarlinkSatFactory.createInstance(item)
          }
          entityList
      case "rockets" =>
          dataAsList.foreach { item =>
              entityList = entityList :+ RocketFactory.createInstance(item)
          }
          entityList
    }   
  }

  def filterEntites(isActive: Boolean, data: List[SpaceEntity], entityType: String): List[SpaceEntity] = {
    entityType match {
      case "starlink" =>
        if (isActive) data.collect { case sat: StarlinkSat if sat.active => sat }
        else data.collect { case sat: StarlinkSat if !sat.active => sat }

      case "rocket" =>
        if (isActive) data.collect { case rocket: Rocket if rocket.active => rocket }
        else data.collect { case rocket: Rocket if !rocket.active => rocket }

      case _ => Nil // Handle other types or provide a default case
    }
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
      //entitiesList = entitiesList :+ entity.toString()
      entitiesList = entitiesList :+ write(entity)

    }
    //val message: String = entitiesList.mkString("[\"", "\",\"", "\"]")
    val message: String = entitiesList.mkString("[", ",", "]")

    val record = new ProducerRecord[String, String](topicName, message)
    producer.send(record)

    producer.close()
  }

}

object SpaceEntityProducerActor {
  def props: Props = Props[SpaceEntityProducerActor]
}