package controller.kafka

import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.time.{Millis, Seconds, Span}

import SpaceData.controller.kafka.SpaceDataProducer
import SpaceData.controller.actor.GetCurrentState
import SpaceData.model.Rocket
import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.wordspec.AnyWordSpecLike
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

import SpaceData.model.{SpaceEntity, StarlinkSat, Rocket}
import akka.testkit.TestActor

class SpaceDataProducerSpec extends TestKit(ActorSystem("SpaceDataProducerSpec"))
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterEach
  with ScalaFutures
  with ImplicitSender
  with Eventually {

  private val testTopic = "test-topic"

  private val producer = new SpaceDataProducer()

  "SpaceDataProducer" should {
    "produce entities to Kafka topic" in {
      // Test entities
      val rocket1 = Rocket("rocket", "Rocket-1", "011", true)
      val rocket2 = Rocket("rocket", "Rocket-2", "012", false)
      val rocket3 = Rocket("rocket", "Rocket-3", "013", true)
      val testEntities = List(rocket1, rocket2, rocket3)//.mapTo(List[SpaceEntity])

      producer.produceEntities(testEntities, testTopic)

      // Initialize the timeout for actor interactions
      implicit val timeout: Timeout = Timeout(10.seconds)

      def afterAll(): Unit = {
        TestKit.shutdownActorSystem(system)
      }

      eventually {
        import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}

        // Set up EmbeddedKafka
        implicit val embeddedKafkaConfig: EmbeddedKafkaConfig =
          EmbeddedKafkaConfig(kafkaPort = 9092, zooKeeperPort = 2181)
        val expectedMessage = "[{\"entityType\":\"rocket\",\"name\":\"Rocket-1\",\"id\":\"011\",\"active\":true},{\"entityType\":\"rocket\",\"name\":\"Rocket-2\",\"id\":\"012\",\"active\":false},{\"entityType\":\"rocket\",\"name\":\"Rocket-3\",\"id\":\"013\",\"active\":true}]"
        EmbeddedKafka.consumeFirstStringMessageFrom(testTopic) shouldBe expectedMessage
      }
    }

    "get and filter entities based on the given criteria" in {
      // Create a test probe for the HttpClientActor
      val httpClientActorProbe = TestProbe()

      httpClientActorProbe.setAutoPilot(new TestActor.AutoPilot {
        def run(sender: ActorRef, msg: Any): TestActor.AutoPilot = {
          msg match {
            case GetCurrentState =>
              // Simulate the response to GetCurrentState
              sender ! List(StarlinkSat("starlink", "Starlink-1", "123", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 1, active = true, None),
                            StarlinkSat("starlink", "Starlink-2", "456", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 0, active = false, None),
                            StarlinkSat("starlink", "Starlink-3", "789", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 1, active = true, None))
              TestActor.KeepRunning
            case _ =>
              TestActor.KeepRunning
          }
        }
      })

      // Call the getAndFilterEntities method
      val futureEntities: Future[List[SpaceEntity]] =
        producer.getAndFilterEntities(isActive = true, httpClientActorProbe.ref, entityType = "starlinksat")

      // Simulate the response from HttpClientActor with a list of SpaceEntities
      // val mockSpaceEntities: List[SpaceEntity] = List(
      //   StarlinkSat("starlink", "Starlink-1", "123", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 1, active = true, None),
      //   StarlinkSat("starlink", "Starlink-2", "456", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 0, active = false, None),
      //   StarlinkSat("starlink", "Starlink-3", "789", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 1, active = true, None)
      // )

      // // Send the mock entities to the test probe
      // httpClientActorProbe.expectMsg(GetCurrentState)
      // httpClientActorProbe.reply(mockSpaceEntities)

      // Wait for the future result and assert the filtered entities
      val resultEntities: List[SpaceEntity] = Await.result(futureEntities, 5.seconds)

      // Assert the expected filtered entities based on the isActive and entityType criteria
      resultEntities shouldBe List(
        StarlinkSat("starlink", "Starlink-1", "123", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 1, active = true, None),
        StarlinkSat("starlink", "Starlink-3", "789", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 1, active = true, None)
      )
    }
  }
}
