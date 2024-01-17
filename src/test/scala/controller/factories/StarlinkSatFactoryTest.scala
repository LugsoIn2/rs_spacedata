import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpecLike
import SpaceData.model.StarlinkSat
import SpaceData.controller.factories.StarlinkSatFactory
import io.circe.Json
import io.circe.parser._

class StarlinkSatFactorySpec extends AnyWordSpecLike {

  "StarlinkSatFactory" should {

    "create a StarlinkSat instance from valid JSON" in {
      val validJson: Json = parse("""
        |{
        |  "spaceTrack": {
        |    "OBJECT_NAME": "Starlink-1",
        |    "LAUNCH_DATE": "2022-01-01",
        |    "PERIOD": 90.0,
        |    "REV_AT_EPOCH": 100,
        |    "DECAYED": 1
        |  },
        |  "id": "123",
        |  "height_km": 500.0,
        |  "latitude": 45.0,
        |  "longitude": 30.0
        |}""".stripMargin).getOrElse(Json.Null)

      val starlinkSat = StarlinkSatFactory.createInstance(validJson)

      starlinkSat.entityType shouldBe "StarlinkSat"
      starlinkSat.name shouldBe "Starlink-1"
      starlinkSat.id shouldBe "123"
      starlinkSat.launchDate shouldBe "2022-01-01"
      starlinkSat.period shouldBe 90.0
      starlinkSat.height shouldBe 500.0
      starlinkSat.latitude shouldBe 45.0
      starlinkSat.longitude shouldBe 30.0
      starlinkSat.earthRevolutions shouldBe 100
      starlinkSat.decayed shouldBe 1
    }

    "create a StarlinkSat instance with default values for missing fields" in {
      val validJson: Json = parse("{}").getOrElse(Json.Null)

      val starlinkSat = StarlinkSatFactory.createInstance(validJson)

      starlinkSat.entityType shouldBe "StarlinkSat"
      starlinkSat.name shouldBe "Unknown"
      starlinkSat.id shouldBe "Unknown"
      starlinkSat.launchDate shouldBe "Unknown"
      starlinkSat.period shouldBe 0.0
      starlinkSat.height shouldBe 0.0
      starlinkSat.latitude shouldBe 0.0
      starlinkSat.longitude shouldBe 0.0
      starlinkSat.earthRevolutions shouldBe 0
      starlinkSat.decayed shouldBe 0
    }

    "create a StarlinkSat instance with default values for invalid JSON" in {
      val invalidJson: Json = parse("invalid json").getOrElse(Json.Null)

      val starlinkSat = StarlinkSatFactory.createInstance(invalidJson)

      starlinkSat.entityType shouldBe "StarlinkSat"
      starlinkSat.name shouldBe "Unknown"
      starlinkSat.id shouldBe "Unknown"
      starlinkSat.launchDate shouldBe "Unknown"
      starlinkSat.period shouldBe 0.0
      starlinkSat.height shouldBe 0.0
      starlinkSat.latitude shouldBe 0.0
      starlinkSat.longitude shouldBe 0.0
      starlinkSat.earthRevolutions shouldBe 0
      starlinkSat.decayed shouldBe 0
    }

  }
}
