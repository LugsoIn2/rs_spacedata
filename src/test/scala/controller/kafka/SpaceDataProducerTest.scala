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
  private val starlinkSatMocks
  = List( StarlinkSat("starlink", "Starlink-1", "123", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 1, active = true, None),
          StarlinkSat("starlink", "Starlink-2", "456", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 0, active = false, None),
          StarlinkSat("starlink", "Starlink-3", "789", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 1, active = true, None))
  private val rocketMocks
  = List( Rocket("rocket", "Rocket-1", "011", true),
          Rocket("rocket", "Rocket-2", "012", false),
          Rocket("rocket", "Rocket-3", "013", true))

  "SpaceDataProducer" should {
    "produce entities to Kafka topic" in {
      producer.produceEntities(rocketMocks, testTopic)
      Thread.sleep(10.seconds.toMillis)

      def afterAll(): Unit = {
        TestKit.shutdownActorSystem(system)
      }

      eventually {
        import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}

        // Set up EmbeddedKafka
        implicit val embeddedKafkaConfig: EmbeddedKafkaConfig = EmbeddedKafkaConfig(kafkaPort = 9092, zooKeeperPort = 2181)
        val expectedMessage = "[{\"entityType\":\"rocket\",\"name\":\"Rocket-1\",\"id\":\"011\",\"active\":true},{\"entityType\":\"rocket\",\"name\":\"Rocket-2\",\"id\":\"012\",\"active\":false},{\"entityType\":\"rocket\",\"name\":\"Rocket-3\",\"id\":\"013\",\"active\":true}]"
        EmbeddedKafka.consumeFirstStringMessageFrom(testTopic) shouldBe expectedMessage
      }
    }

    "produce entities to all desired topics" in {
      producer.produceEntityToKafka()
      Thread.sleep(10.seconds.toMillis)

      eventually {
        import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}

        // Set up EmbeddedKafka
        implicit val embeddedKafkaConfig: EmbeddedKafkaConfig = EmbeddedKafkaConfig(kafkaPort = 9092, zooKeeperPort = 2181)
        EmbeddedKafka.consumeFirstStringMessageFrom("starlinksats-all") shouldBe a[String]
        EmbeddedKafka.consumeFirstStringMessageFrom("starlinksats-active") shouldBe a[String]
        EmbeddedKafka.consumeFirstStringMessageFrom("starlinksats-inactive") shouldBe a[String]
        EmbeddedKafka.consumeFirstStringMessageFrom("rockets-all") shouldBe a[String]
        EmbeddedKafka.consumeFirstStringMessageFrom("rockets-active") shouldBe a[String]
        EmbeddedKafka.consumeFirstStringMessageFrom("rockets-inactive") shouldBe a[String]
      }
    }

    "get and filter active entities" in {
      // Create a test probe for the HttpClientActor
      val httpClientActorProbe = TestProbe()

      httpClientActorProbe.setAutoPilot(new TestActor.AutoPilot {
        def run(sender: ActorRef, msg: Any): TestActor.AutoPilot = {
          msg match {
            case GetCurrentState =>
              // Simulate the response to GetCurrentState
              sender ! starlinkSatMocks
              TestActor.KeepRunning
            case _ =>
              TestActor.KeepRunning
          }
        }
      })

      // Call the getAndFilterEntities method
      val futureEntities: Future[List[SpaceEntity]] =
        producer.getAndFilterEntities(isActive = true, httpClientActorProbe.ref, entityType = "starlinksat")

      // Wait for the future result and assert the filtered entities
      val resultEntities: List[SpaceEntity] = Await.result(futureEntities, 5.seconds)

      // Assert the expected filtered entities based on the isActive and entityType criteria
      resultEntities shouldBe List(
        StarlinkSat("starlink", "Starlink-1", "123", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 1, active = true, None),
        StarlinkSat("starlink", "Starlink-3", "789", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 1, active = true, None)
      )
    }

    "get and filter inactive entities" in {
      // Create a test probe for the HttpClientActor
      val httpClientActorProbe = TestProbe()

      httpClientActorProbe.setAutoPilot(new TestActor.AutoPilot {
        def run(sender: ActorRef, msg: Any): TestActor.AutoPilot = {
          msg match {
            case GetCurrentState =>
              // Simulate the response to GetCurrentState
              sender ! starlinkSatMocks
              TestActor.KeepRunning
            case _ =>
              TestActor.KeepRunning
          }
        }
      })

      // Call the getAndFilterEntities method
      val futureEntities: Future[List[SpaceEntity]] =
        producer.getAndFilterEntities(isActive = false, httpClientActorProbe.ref, entityType = "starlinksat")

      // Wait for the future result and assert the filtered entities
      val resultEntities: List[SpaceEntity] = Await.result(futureEntities, 5.seconds)

      // Assert the expected filtered entities based on the isActive and entityType criteria
      resultEntities shouldBe List(
        StarlinkSat("starlink", "Starlink-2", "456", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 0, active = false, None)
      )
    }
  }
}
