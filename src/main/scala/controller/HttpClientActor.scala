package SpaceData.controller

import akka.actor.{Actor, ActorSystem, Props, ActorLogging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import SpaceData.controller.factories.{StarlinkSatFactory, RocketFactory}
import SpaceData.util._

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

import SpaceData.model.{SpaceEntity}

import io.circe.Json
import io.circe.parser._
import io.circe._

// Message to trigger HTTP request in actor
case class GetSpaceEntities(endpoint: String)

// Message to return actor's current state as a response
case object GetCurrentState

// Actor responsible for making HTTP requests and maintaining state
class HttpClientActor extends Actor with ActorLogging {
  implicit val system: ActorSystem = context.system
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = context.dispatcher
  val hostUrl: String = "https://api.spacexdata.com/v4"

  // Initial state
  var spaceEntities: List[SpaceEntity] = List.empty

  def receive: Receive = {
    case GetSpaceEntities(endpoint) =>
      val request = HttpRequest(uri = hostUrl + endpoint)
      val responseFuture = Http().singleRequest(request)

      responseFuture.onComplete {
        case Success(response) =>
            val data = Unmarshal(response.entity).to[String]
            data.onComplete {
                case Success(body) =>
                  spaceEntities = createSpaceEntitiesInstances(endpoint, body)
                  sender() ! spaceEntities
                case Failure(ex) =>
                  println(s"Failed to unmarshal response body: $ex")
                  spaceEntities = List.empty
                  sender() ! spaceEntities
            }

        case Failure(ex) =>
          println(s"Request to $endpoint failed: $ex")
      }

    case GetCurrentState =>
      // Respond with the current state
      sender() ! spaceEntities
  }

  
  def createSpaceEntitiesInstances(endpoint: String, body: String): List[SpaceEntity] = {
    val entityList: List[SpaceEntity] = endpoint match {
      case "/starlink" => parseToList(body).map(item => StarlinkSatFactory.createInstance(item))
      case "/rockets"  => parseToList(body).map(item => RocketFactory.createInstance(item))
      case _           => List.empty
    }
    entityList
  }

  def parseToList(json: String): List[io.circe.Json] = {
    parse(json) match {
      case Right(json) => json.asArray.getOrElse(Vector.empty).toList
      case Left(error) =>
        println(s"Failed to parse JSON: $error")
        List.empty
    }
  }
}

object HttpClientActor {
  def props: Props = Props[HttpClientActor]
}