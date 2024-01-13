package SpaceData.controller

import akka.actor.{Actor, ActorSystem, Props, ActorLogging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

import SpaceData.model.{Launch, StarlinkSat, SpaceEntity}

import akka.stream.StreamRefMessages
import akka.http.scaladsl.unmarshalling.Unmarshal

import io.circe.Json

// Message to trigger HTTP request in actor
case class GetSpaceEntities(endpoint: String)

// Message to return actor's current state as a response
case object GetCurrentState //():List[SpaceEntity]

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
            val data = Unmarshal(response.entity).to[String] //response.entity.toString()
            data.onComplete {
                case Success(body) =>
                  spaceEntitiesDiff(endpoint, body)
                case Failure(ex) =>
                    println(s"Failed to unmarshal response body: $ex")
            }
            
            
        case Failure(ex) =>
          println(s"Request to $endpoint failed: $ex")
      }

    case GetCurrentState =>
      // Respond with the current state
      sender() ! spaceEntities
  }


  
  def spaceEntitiesDiff(endpoint: String, body: String) {
    val dataAsList: List[Json] = SpaceData.util.spacexApiClient.Helpers.parseToList(body, "get")
    var entityList: List[SpaceEntity] = List.empty
    spaceEntities = endpoint match {
      case "/starlink" =>
          dataAsList.foreach { item =>
              entityList = entityList :+ StarlinkSatFactory.createInstance(item)
          }
          entityList
      case "/rockets" =>
          dataAsList.foreach { item =>
              entityList = entityList :+ RocketFactory.createInstance(item)
          }
          entityList
    }   
  }

}

object HttpClientActor {
  def props: Props = Props[HttpClientActor]
}

// object ParallelHttpCalls {
//   def main(args: Array[String]): Unit = {
//     implicit val actorSystem: ActorSystem = ActorSystem("ParallelHttpCalls")
//     implicit val materializer: ActorMaterializer = ActorMaterializer()
//     val httpClientActor = actorSystem.actorOf(Props(new HttpClientActor))
    
//     val urls = List(
//       "https://api.example.com/endpoint1",
//       "https://api.example.com/endpoint2",
//       "https://api.example.com/endpoint3"
//       // Add more URLs as needed
//     )

//     // Trigger HTTP requests using actors
//     urls.foreach(url => httpClientActor ! HttpRequestMessage(url))

//     // Get the current state of the actor
//     httpClientActor ! GetCurrentState
//   }
// }
