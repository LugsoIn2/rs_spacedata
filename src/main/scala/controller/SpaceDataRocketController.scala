package SpaceData.controller

//import model.StarlinkSat
import SpaceData.model.Rocket
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

object SpaceDataRocketController {

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

    def createInstance(json: io.circe.Json): Rocket = {
        val rocket = Rocket(
            name = json.hcursor.downField("name").as[String].getOrElse("Unknown"),
            id = json.hcursor.downField("id").as[String].getOrElse("Unknown"),
            active = json.hcursor.downField("active").as[Boolean].getOrElse(false)
        )
        rocket
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
