import akka.stream.ActorMaterializer
import akka.actor.{ActorSystem, Props}
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import akka.pattern.ask
import akka.util.Timeout

object Main extends App {
  println("Hello, World!")

  implicit val system: ActorSystem = ActorSystem("APIActorSystem")

  val apiClientActor = system.actorOf(Props[APIClientActor], "apiClientActor")

  // Sending a message to the actor to initiate the API call
  implicit val timeout: Timeout = Timeout(10.seconds)
  val futureResult = apiClientActor ? FetchData

  // Wait until the FetchData message is processed by the actor
  try {
    Await.result(futureResult, 10.seconds)
  } catch {
    case ex: Exception =>
      println(s"An error occurred: ${ex.getMessage}")
  } finally {
    // Terminate the actor system after FetchData is executed or timed out
    system.terminate()
  }
}

