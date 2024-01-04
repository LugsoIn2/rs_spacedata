import model.StarlinkSat

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.{HttpURLConnection, URL}

import io.circe.parser._
import io.circe._

object SpaceX_API {

    
    //var launches: List[Launch] = List().empty

    def starlink(slct: Selector): List[StarlinkSat] = {
        val response: String = HttpClient.getStarlink(slct)
        val starlinkSatsListJson: List[io.circe.Json] = parseToList(response)
        var starlinkSats: List[StarlinkSat] = List().empty
        if (starlinkSatsListJson.nonEmpty) {
            starlinkSatsListJson.foreach { item =>
                val starlinkSat: StarlinkSat = createInstanceStarlinkSat(item)
                starlinkSats = starlinkSats :+ starlinkSat
            }
        }
        starlinkSats
    }

    def createInstanceStarlinkSat(json: io.circe.Json): StarlinkSat = {
        val starlinkSat = StarlinkSat(
            name = json.hcursor.downField("spaceTrack").downField("OBJECT_NAME").as[String].getOrElse("Unknown"),
            launchDate = json.hcursor.downField("spaceTrack").downField("LAUNCH_DATE").as[String].getOrElse("Unknown"),
            period = json.hcursor.downField("spaceTrack").downField("PERIOD").as[String].getOrElse("Unknown"),
            height = json.hcursor.downField("height_km").as[Int].getOrElse(0),
            latitude = json.hcursor.downField("latitude").as[Double].getOrElse(0),
            longitude = json.hcursor.downField("longitude").as[Double].getOrElse(0),
            earthRevolutions = json.hcursor.downField("spaceTrack").downField("MEAN_MOTION").as[Int].getOrElse(0)
        )
        starlinkSat
    }

    def parseToList(json: String): List[io.circe.Json] = {
        val parsedJson: Either[io.circe.Error, Json] = parse(json)
        parsedJson match {
            case Right(json) =>
                val items: List[Json] = json.asArray.getOrElse(Vector.empty).toList
                println(s"Found ${items.length} items")
                return items

            case Left(error) =>
            println(s"Failed to parse JSON: $error")
        }
        List.empty
    }

    /*def getAllLaunches(): List[Launch] = {
        launches
    }*/
}

object HttpClient {

    def executeRequest(url: URL): String = {
        val connection = url.openConnection().asInstanceOf[HttpURLConnection]

        connection.setRequestMethod("GET")

        val responseCode = connection.getResponseCode
        println(s"Response Code: $responseCode")

        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.getInputStream
            val reader = new BufferedReader(new InputStreamReader(inputStream))
            var inputLine: String = null

            val response = new StringBuilder()

            while ({inputLine = reader.readLine(); inputLine != null}) {
                response.append(inputLine)
            }

            reader.close()
            response.toString
            
        } else {
            println("HTTP GET request failed")
            "None"
        }
        
    }

  def getStarlink(slct: Selector): String = slct match {
    case all => {
        val url = new URL("https://api.spacexdata.com/v4/starlink")
        executeRequest(url)
    } case active => {
        val url = new URL("https://api.spacexdata.com/v4/starlink") // Replace with corresponding API query
        executeRequest(url)
    } case inactive => {
        val url = new URL("https://api.spacexdata.com/v4/starlink") // Replace with corresponding API query
        executeRequest(url)
    }
  }

  def getLaunches(): String = {
    val url = new URL("https://api.spacexdata.com/v4/launches") // Replace with your API endpoint
    executeRequest(url)
  }
}
