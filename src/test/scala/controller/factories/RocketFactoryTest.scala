package controller.factories

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpecLike
import SpaceData.model.Rocket
import SpaceData.controller.factories.RocketFactory
import io.circe.Json
import io.circe.parser._

class RocketFactorySpec extends AnyWordSpecLike {

  "RocketFactory" should {

    "create a Rocket instance from valid JSON" in {
      val validJson: Json = parse("""{"name": "Falcon 9", "id": "123", "active": true}""").getOrElse(Json.Null)

      val rocket = RocketFactory.createInstance(validJson)

      rocket.entityType shouldBe "Rocket"
      rocket.name shouldBe "Falcon 9"
      rocket.id shouldBe "123"
      rocket.active shouldBe true
    }

    "create a Rocket instance with default values for missing fields" in {
      val validJson: Json = parse("{}").getOrElse(Json.Null)

      val rocket = RocketFactory.createInstance(validJson)

      rocket.entityType shouldBe "Rocket"
      rocket.name shouldBe "Unknown"
      rocket.id shouldBe "Unknown"
      rocket.active shouldBe false
    }

    "create a Rocket instance with default values for invalid JSON" in {
      val invalidJson: Json = parse("invalid json").getOrElse(Json.Null)

      val rocket = RocketFactory.createInstance(invalidJson)

      rocket.entityType shouldBe "Rocket"
      rocket.name shouldBe "Unknown"
      rocket.id shouldBe "Unknown"
      rocket.active shouldBe false
    }

  }
}
