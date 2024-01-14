package SpaceData.controller

import SpaceData.model.Rocket

import io.circe.parser._
import io.circe._

object RocketFactory {

    def createInstance(json: io.circe.Json): Rocket = {
        val rocket = Rocket(
            entityType = "Rocket",
            name = json.hcursor.downField("name").as[String].getOrElse("Unknown"),
            id = json.hcursor.downField("id").as[String].getOrElse("Unknown"),
            active = json.hcursor.downField("active").as[Boolean].getOrElse(false)
        )
        rocket
    }
}
