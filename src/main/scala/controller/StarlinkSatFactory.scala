package SpaceData.controller

//import model.StarlinkSat
import SpaceData.model.StarlinkSat
// import SpaceData.util.spacexApiClient.SelectorStarlinkSat
// import SpaceData.util.spacexApiClient.SpaceXApiClient
// import SpaceData.util.spacexApiClient.Helpers._

// import scala.concurrent.{ExecutionContext, Future}
// import scala.util.{Failure, Success}

// import java.io.BufferedReader
// import java.io.InputStreamReader
// import java.net.{HttpURLConnection, URL}

import io.circe.parser._
import io.circe._

object StarlinkSatFactory {

    /*def starlink(slct: SelectorStarlinkSat): List[StarlinkSat] = {
        //val response: String = 
        val starlinkSatsListJson: List[io.circe.Json] = SpaceXApiClient.getStarlink(slct)
        var starlinkSats: List[StarlinkSat] = List().empty
        if (starlinkSatsListJson.nonEmpty) {
            starlinkSatsListJson.foreach { item =>
                val starlinkSat: StarlinkSat = createInstance(item)
                starlinkSats = starlinkSats :+ starlinkSat
            }
        }
        starlinkSats
    } */

    def createInstance(json: io.circe.Json): StarlinkSat = {
        val starlinkSat = StarlinkSat(
            entityType = "StarlinkSat",
            name = json.hcursor.downField("spaceTrack").downField("OBJECT_NAME").as[String].getOrElse("Unknown"),
            id = json.hcursor.downField("id").as[String].getOrElse("Unknown"),
            launchDate = json.hcursor.downField("spaceTrack").downField("LAUNCH_DATE").as[String].getOrElse("Unknown"),
            period = json.hcursor.downField("spaceTrack").downField("PERIOD").as[Double].getOrElse(0),
            height = json.hcursor.downField("height_km").as[Double].getOrElse(0),
            latitude = json.hcursor.downField("latitude").as[Double].getOrElse(0),
            longitude = json.hcursor.downField("longitude").as[Double].getOrElse(0),
            earthRevolutions = json.hcursor.downField("spaceTrack").downField("REV_AT_EPOCH").as[Int].getOrElse(0),
            decayed = json.hcursor.downField("spaceTrack").downField("DECAYED").as[Int].getOrElse(0)
        )
        starlinkSat
    }

    // def parseToList(json: String): List[io.circe.Json] = {
    //     val parsedJson: Either[io.circe.Error, Json] = parse(json)
    //     parsedJson match {
    //         case Right(json) =>
    //             val items: List[Json] = json.asArray.getOrElse(Vector.empty).toList
    //             println(s"Found ${items.length} items")
    //             return items

    //         case Left(error) =>
    //         println(s"Failed to parse JSON: $error")
    //     }
    //     List.empty
    // }

    /*def getAllLaunches(): List[Launch] = {
        launches
    }*/
}
