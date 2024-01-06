package SpaceData.controller

import SpaceData.model.Launch
import SpaceData.util.dsl.SelectorStarlinkSat
import SpaceData.util.dsl.SelectorLaunch
import SpaceData.util.dsl.SpaceXApiClient
import SpaceData.util.Helpers._


object SpaceDataLaunchController {

    def launches(slct: SelectorLaunch): List[Launch] = {
        val launchesListJson: List[io.circe.Json] = SpaceXApiClient.getLaunches(slct)
        var launches: List[Launch] = List().empty
        if (launchesListJson.nonEmpty) {
            launchesListJson.foreach { item =>
                val launch: Launch = createInstanceLaunch(item)
                launches = launches :+ launch
            }
        }
        launches
    }

    def createInstanceLaunch(json: io.circe.Json): Launch = {
        val launch = Launch(
            name = json.hcursor.downField("name").as[String].getOrElse("Unknown"),
            id = json.hcursor.downField("id").as[String].getOrElse("Unknown"),
            date_utc = json.hcursor.downField("date_utc").as[String].getOrElse("Unknown"),
            launchpad = json.hcursor.downField("launchpad").as[String].getOrElse("Unknown"),
            success = json.hcursor.downField("success").as[Boolean].getOrElse(false)
        )
        launch
    }
}



