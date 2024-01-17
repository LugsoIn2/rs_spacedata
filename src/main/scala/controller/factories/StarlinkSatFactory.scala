package SpaceData.controller.factories

import SpaceData.model.StarlinkSat

import io.circe.parser._
import io.circe._

object StarlinkSatFactory {

    def createInstance(json: io.circe.Json): StarlinkSat = {
        val starlinkSat = StarlinkSat(
            entityType = "StarlinkSat",
            name = json.hcursor.downField("spaceTrack").downField("OBJECT_NAME").as[String].getOrElse("Unknown"),
            id = json.hcursor.downField("id").as[String].getOrElse("Unknown"),
            launchDate = json.hcursor.downField("spaceTrack").downField("LAUNCH_DATE").as[String].getOrElse("Unknown"),
            period = json.hcursor.downField("spaceTrack").downField("PERIOD").as[Double].getOrElse(0),
            height = Option(json.hcursor.downField("height_km").as[Double].getOrElse(null.asInstanceOf[Double])).getOrElse(0.0),
            latitude = Option(json.hcursor.downField("latitude").as[Double].getOrElse(null.asInstanceOf[Double])).getOrElse(0.0),
            longitude = Option(json.hcursor.downField("longitude").as[Double].getOrElse(null.asInstanceOf[Double])).getOrElse(0.0),
            earthRevolutions = json.hcursor.downField("spaceTrack").downField("REV_AT_EPOCH").as[Int].getOrElse(0),
            decayed = json.hcursor.downField("spaceTrack").downField("DECAYED").as[Int].getOrElse(0)
        )
        starlinkSat
    }
}
