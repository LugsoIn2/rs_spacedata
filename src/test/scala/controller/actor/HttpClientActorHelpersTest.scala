package controller.actor

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpecLike
import SpaceData.controller.actor.HttpClientActorHelpers
import SpaceData.model.SpaceEntity
import io.circe.Json
import io.circe.parser._

class HttpClientActorSpec extends AnyWordSpecLike {

  "HttpClientActorHelpers" should {

    "create list of SpaceEntities for /starlink endpoint" in {
      val jsonString = """[{"id": "1", "name": "Starlink1", "type": "satellite", "active": true}]"""

      val result = HttpClientActorHelpers.createSpaceEntitiesInstances("/starlink", jsonString)

      result shouldBe a[List[_]]
      result.head shouldBe a[SpaceEntity] // Assuming SpaceEntity is the common trait for StarlinkSat and Rocket
      // Add more assertions based on the expected result
    }

    "create list of SpaceEntities for /rockets endpoint" in {
      val jsonString = """[{"id": "1", "name": "Rocket1", "active": true}]"""

      val result = HttpClientActorHelpers.createSpaceEntitiesInstances("/rockets", jsonString)

      result shouldBe a[List[_]]
      result.head shouldBe a[SpaceEntity] // Assuming SpaceEntity is the common trait for StarlinkSat and Rocket
      // Add more assertions based on the expected result
    }
  
    "return empty list for unknown endpoint" in {
      val jsonString = """[{"id": "1", "name": "UnknownEntity", "type": "unknown", "active": true}]"""

      val result = HttpClientActorHelpers.createSpaceEntitiesInstances("/unknown", jsonString)

      result shouldBe empty
    }
    "parse valid JSON to a list of Json" in {
      val validJsonString = """[{"id": "1", "name": "Starlink1", "type": "satellite", "active": true}]"""
      val expectedJsonList = List(
        Json.obj(
          "id" -> Json.fromString("1"),
          "name" -> Json.fromString("Starlink1"),
          "type" -> Json.fromString("satellite"),
          "active" -> Json.fromBoolean(true)
        )
      )
      HttpClientActorHelpers.parseToList(validJsonString) shouldEqual expectedJsonList
    }
    "parse invalid JSON to a list of Json" in {
      val invalidJsonString = """[{"id": "1", "name": "Starlink1", "type": "satellite" "active": true}]"""
      HttpClientActorHelpers.parseToList(invalidJsonString) shouldBe empty
    }
  }
}
