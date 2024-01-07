package util.spacexApiClient

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import scala.concurrent.{ExecutionContextExecutor, Future}
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.ContentTypes
//import java.util.concurrent.Future

object ActorSystemController {
    implicit val actorSystem: ActorSystem = ActorSystem("HTTPRequestsActorSystem")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher
  
    def getData(): Unit = {
        // Trigger HTTP requests by sending a PerformRequests message to an actor
        val urls = List("https://api.example.com/endpoint1", "https://api.example.com/endpoint2") 
    }

    def postCall(host: String, requestURL: String, payload: String) : Unit = {
        val request = HttpRequest(
            method = HttpMethods.POST,
            uri = host + requestURL,
            entity = HttpEntity(ContentTypes.`application/json`, payload)
        )
        
        val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
        responseFuture.onComplete{
            case Success(res) =>
                if (res.status == StatusCodes.OK) {
                    val responseBody : Future[String] = Unmarshal(res.entity).to[String]
                    responseBody.onComplete{
                        case Success(body) =>
                        val JsonRes = Json.parse(body)
                        println("post: " + body)
                        updateField(JsonRes)
                        case Failure(_) => println("Post Call Error")
                    }
                }
            }
    }

    def getCall(host: String, requestURL: String) : Unit = {
        implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "SingleRequest")
        // needed for the future flatMap/onComplete in the end
        implicit val executionContext: ExecutionContextExecutor = system.executionContext
        val responseFuture: Future[HttpResponse] = Http().singleRequest(Get(host + requestURL))
        responseFuture.onComplete{
        case Success(res) =>
            val entityAsText : Future[String] = Unmarshal(res.entity).to[String]
            entityAsText.onComplete{
            case Success(body) =>
                if (body == "Saved Success") {
                println(body)
                } else {
                val JsonRes = Json.parse(body)
                println("get: " + JsonRes)
                updateField(JsonRes)
                }
            case Failure(_) => println("Get Call Error")
            }
        case Failure(_) => sys.error("something wrong")
        }
    }

}