import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

sealed trait HttpRequestMessage
case class GetRequest(url: String) extends HttpRequestMessage
case class PostRequest(url: String, payload: String) extends HttpRequestMessage

class HttpClientActor extends Actor {
  implicit val system: ActorSystem = context.system
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = context.dispatcher


  def receive: Receive = {
    case GetRequest(url) =>
      val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
      responseFuture.pipeTo(sender())

    case PostRequest(url, payload) =>
      val request = HttpRequest(
        method = HttpMethods.POST,
        uri = url,
        entity = HttpEntity(ContentTypes.`application/json`, payload)
      )
      val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
      responseFuture.pipeTo(sender())
  }
}

object ParallelHttpRequests {
  def doRequests(): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem("ParallelHttpRequests")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    val httpClientActor = actorSystem.actorOf(Props[HttpClientActor], "httpClientActor")

    val urlsGet = List(
      "https://api.spacexdata.com/v4/starlink",
      "https://api.spacexdata.com/v4/launches"
      // Add more URLs as needed
    )

    val urlsPost = List(
      "https://api.spacexdata.com/v4/starlink/query", // active
      "https://api.spacexdata.com/v4/starlink/query" // inactive
      //"https://api.spacexdata.com/v4/launches/query"
      // Add more URLs as needed
    )

    val payloads = List(
      """{"key1": "value1"}""",
      """{"key2": "value2"}"""
      // Add more payloads for POST requests
    )

    implicit val timeout: akka.util.Timeout = 10.seconds

    val getResponses: Future[List[HttpResponse]] = Future.sequence(
      urlsGet.map(url => (httpClientActor ? GetRequest(url)).mapTo[HttpResponse])
    )

    val postResponses: Future[List[HttpResponse]] = Future.sequence(
      urlsPost.zip(payloads).map { case (url, payload) =>
        (httpClientActor ? PostRequest(url, payload)).mapTo[HttpResponse]
      }
    )

    val allResponses: Future[(List[HttpResponse], List[HttpResponse])] = for {
      getResp <- getResponses
      postResp <- postResponses
    } yield (getResp, postResp)

    allResponses.onComplete {
      case Success((getResponses, postResponses)) =>
        getResponses.foreach { response =>
          println(s"GET Response status: ${response.status}")
          // Process the GET response as needed
        }

        postResponses.foreach { response =>
          println(s"POST Response status: ${response.status}")
          // Process the POST response as needed
        }
        actorSystem.terminate()

      case Failure(ex) =>
        println(s"Failed to get responses: $ex")
        actorSystem.terminate()
    }
  }
}
