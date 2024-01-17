package model

import SpaceData.model.{Rocket, SpaceEntity}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

class RocketSpec extends AnyWordSpec with Matchers {

  "Rocket" should {

    "be serializable to JSON" in {
      val rocket = Rocket(entityType = "Rocket", name = "Falcon 9", id = "123", active = true)
      val expectedJson = Json.obj(
        "entityType" -> "Rocket",
        "name" -> "Falcon 9",
        "id" -> "123",
        "active" -> true
      )

      Json.toJson(rocket) shouldEqual expectedJson
    }

    "be deserializable from JSON" in {
      val json = Json.obj(
        "entityType" -> "Rocket",
        "name" -> "Falcon 9",
        "id" -> "123",
        "active" -> true
      )
      val expectedRocket = Rocket(entityType = "Rocket", name = "Falcon 9", id = "123", active = true)

      json.as[Rocket] shouldEqual expectedRocket
    }

    "have a valid toString representation" in {
      val rocket = Rocket(entityType = "Rocket", name = "Falcon 9", id = "123", active = true)
      val expectedToString = "ID: 123\nName: Falcon 9\nActive: true"

      rocket.toString shouldEqual expectedToString
    }

  }
}
