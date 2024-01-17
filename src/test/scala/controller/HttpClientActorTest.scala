import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import akka.util.ByteString
import akka.pattern.ask
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.{Source, Sink, Keep}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.Eventually._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.BeforeAndAfterAll
import SpaceData.controller.actor.{GetSpaceEntities, GetCurrentState, HttpClientActor}
import SpaceData.model.{SpaceEntity, StarlinkSat, Rocket}
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._

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
    "return space entities on GetSpaceEntities(\"/starlink\") message" in {
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

    "GetCurrentState of HattpClientActorRockets" in {
      val actorRef = system.actorOf(HttpClientActor.props)
      actorRef ! GetSpaceEntities("/rockets")
      // Wait a couple of seconds to allow the actor to process the message
      Thread.sleep(5000)
      
      val futureSpaceEntities: Future[List[SpaceEntity]] = (actorRef ? GetCurrentState)
        .mapTo[List[SpaceEntity]]

      // Using ScalaTest's built-in eventually to handle asynchronous assertions
      eventually {
        futureSpaceEntities.onComplete { entities =>
          entities.asInstanceOf[List[SpaceEntity]] should not be empty 
          entities.asInstanceOf[List[SpaceEntity]].head shouldBe a[Rocket]
        }
      }
    }

    "GetCurrentState of HattpClientActorStarlinkSats" in {
      val actorRef = system.actorOf(HttpClientActor.props)
      actorRef ! GetSpaceEntities("/starlink")
      // Wait a couple of seconds to allow the actor to process the message
      Thread.sleep(5000)
      
      val futureSpaceEntities: Future[List[SpaceEntity]] = (actorRef ? GetCurrentState)
        .mapTo[List[SpaceEntity]]

      // Using ScalaTest's built-in eventually to handle asynchronous assertions
      eventually {
        futureSpaceEntities.onComplete { entities =>
          entities.asInstanceOf[List[SpaceEntity]].length should not be 0 
          entities.asInstanceOf[List[SpaceEntity]].head shouldBe a[Rocket]
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
    "return space entities on GetSpaceEntities(\"/rockets\") message" in {
      val actorRef = system.actorOf(HttpClientActor.props)
      val futureSpaceEntities: Future[List[SpaceEntity]] = (actorRef ? GetSpaceEntities("/rockets"))
        .mapTo[List[SpaceEntity]]

      // Using ScalaTest's built-in eventually to handle asynchronous assertions
      eventually {
        futureSpaceEntities.onComplete { entities =>
          entities.asInstanceOf[List[SpaceEntity]] should not be empty 
          entities.asInstanceOf[List[SpaceEntity]].head shouldBe a[StarlinkSat]
        }
      }
    }
  }

  def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
}