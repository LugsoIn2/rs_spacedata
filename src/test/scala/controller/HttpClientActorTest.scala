import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import akka.util.ByteString
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.time.{Millis, Seconds, Span}
import SpaceData.controller.{GetSpaceEntities, GetCurrentState, HttpClientActor}
import SpaceData.model.{SpaceEntity, StarlinkSat}
import akka.pattern.ask
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.stream.scaladsl.Source
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.HttpEntity.Strict
import akka.http.scaladsl.unmarshalling.Unmarshal
import io.circe.Json
//import io.circe.literal._
import io.circe.parser._
import org.scalatest.concurrent.Eventually._

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import akka.http.scaladsl.model.ContentTypes
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Keep
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._

class HttpClientActorSpec extends TestKit(ActorSystem("HttpClientActorSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with ScalaFutures {

  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = Timeout(10.seconds)

  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = scaled(Span(5000, Millis)), interval = scaled(Span(100, Millis)))

  "HttpClientActor" should {
    "return space entities on GetSpaceEntities message" in {
      val actorRef = system.actorOf(HttpClientActor.props)
      val futureSpaceEntities: Future[List[SpaceEntity]] = (actorRef ? GetSpaceEntities("/starlink"))
        .mapTo[List[SpaceEntity]]

      // Using ScalaTest's built-in eventually to handle asynchronous assertions
      eventually {
        futureSpaceEntities.onComplete { entities =>
          entities.asInstanceOf[List[SpaceEntity]] should not be empty 
          entities.asInstanceOf[List[SpaceEntity]].head shouldBe a[StarlinkSat]
        }
      }
    }

    "handle failure to unmarshal response body" in {
      val actorRef = system.actorOf(HttpClientActor.props)
      val jsonBody = """[{"id": "1", "name": "Starlink1", "type": "satellite", "active": true}]"""
      val endpoint = "/starlink"

      // Mocking the HTTP response with a failure
      val response = HttpResponse(status = StatusCodes.InternalServerError)

      val result: Future[List[SpaceEntity]] = Source
        .single(response)
        .mapAsync(1)(_ => Unmarshal(response.entity).to[String])
        .map(body => actorRef ! GetSpaceEntities(endpoint))
        .toMat(Sink.ignore)(Keep.right)  // Use Sink.ignore to discard the elements and only keep the completion stage
        .run()
        .flatMap(_ => (actorRef ? GetCurrentState).mapTo[List[SpaceEntity]])

    whenReady(result/*.asInstanceOf[Future[List[SpaceEntity]]]*/) { entities =>
        entities shouldBe empty
      }
    }
  }

  def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
}
