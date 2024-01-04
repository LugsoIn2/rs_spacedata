import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

case object FetchData

class APIClientActor extends Actor with ActorLogging {
  implicit val system: ActorSystem = context.system
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = context.dispatcher

  override def receive: Receive = {
    case FetchData =>
      val url = "https://api.spacexdata.com/v5/launches/"
      //val url = "skldmclskml"
      println("URL: " + url)
      val request = HttpRequest(uri = url)
      println("Fetch data from API!")
      
      val responseFuture = Http().singleRequest(request)

      responseFuture.onComplete {
        case Success(response) =>
          // Handle successful response here, e.g., process the data or send it to another actor
          // For example, printing the response status code
          // Print the response data
          log.info(response.entity.toString())
          response.discardEntityBytes() // Discard the response entity here if not needed
          log.info(s"Received response with status: ${response.status}")
          sender() ! response // Sending the response back to the sender

        case Failure(ex) =>
          println("Failed to fetch data from API")
          log.error(ex.toString(), "Failed to fetch data from API")
          sender() ! akka.actor.Status.Failure(ex) // Sending a failure message back to the sender
      }      
  }
}

object APIClientActor {
  def props: Props = Props[APIClientActor]
}
