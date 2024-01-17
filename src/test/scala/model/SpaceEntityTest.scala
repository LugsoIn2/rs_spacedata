package model

import SpaceData.model.{Rocket, StarlinkSat, SpaceEntity}

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import play.api.libs.json.JsError

class SpaceEntitySpec extends AnyWordSpec with Matchers {

  "SpaceEntity" should {

    "be deserializable to Rocket from JSON" in {
      val json = Json.obj(
        "entityType" -> "Rocket",
        "name" -> "Falcon 9",
        "id" -> "123",
        "active" -> true
      )
      val expectedRocket = Rocket(entityType = "Rocket", name = "Falcon 9", id = "123", active = true)

      json.as[SpaceEntity] shouldEqual expectedRocket
    }

    "be deserializable to StarlinkSat from JSON" in {
      val json = Json.obj(
        "entityType" -> "StarlinkSat",
        "name" -> "Starlink-1",
        "id" -> "456",
        "launchDate" -> "2022-01-01",
        "period" -> 90.5,
        "height" -> 550.0,
        "latitude" -> 40.0,
        "longitude" -> -120.0,
        "earthRevolutions" -> 500,
        "decayed" -> 0
      )
      val expectedStarlinkSat = StarlinkSat(
        entityType = "StarlinkSat",
        name = "Starlink-1",
        id = "456",
        launchDate = "2022-01-01",
        period = 90.5,
        height = 550.0,
        latitude = 40.0,
        longitude = -120.0,
        earthRevolutions = 500,
        decayed = 0
      )

      json.as[SpaceEntity] shouldEqual expectedStarlinkSat
    }

    "return an error for an unknown entity type during deserialization" in {
      val json = Json.obj(
        "entityType" -> "UnknownType",
        "name" -> "Unknown",
        "id" -> "789"
      )

      json.validate[SpaceEntity] shouldBe a[JsError]
    }

    "return an error for missing entityType field during deserialization" in {
      val json = Json.obj(
        "name" -> "MissingType",
        "id" -> "999"
      )

      json.validate[SpaceEntity] shouldBe a[JsError]
    }

    "be serializable to JSON for Rocket" in {
      val rocket = Rocket(entityType = "Rocket", name = "Falcon 9", id = "123", active = true)
      val expectedJson = Json.obj(
        "entityType" -> "Rocket",
        "name" -> "Falcon 9",
        "id" -> "123",
        "active" -> true
      )

      Json.toJson(rocket) shouldEqual expectedJson
    }

    "be serializable to JSON for StarlinkSat" in {
      val starlinkSat = StarlinkSat(
        entityType = "StarlinkSat",
        name = "Starlink-1",
        id = "456",
        launchDate = "2022-01-01",
        period = 90.5,
        height = 550.0,
        latitude = 40.0,
        longitude = -120.0,
        earthRevolutions = 500,
        decayed = 0
      )
      val expectedJson = Json.obj(
        "entityType" -> "StarlinkSat",
        "name" -> "Starlink-1",
        "id" -> "456",
        "launchDate" -> "2022-01-01",
        "period" -> 90.5,
        "height" -> 550.0,
        "latitude" -> 40.0,
        "longitude" -> -120.0,
        "earthRevolutions" -> 500,
        "decayed" -> 0
      )

      Json.toJson(starlinkSat) shouldEqual expectedJson
    }

  }
}