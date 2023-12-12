import model.StarlinkSat

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.{HttpURLConnection, URL}

import io.circe.parser._
import io.circe._

object SpaceX_API {

    var starlinkSats: List[StarlinkSat] = List().empty
    //var launches: List[Launch] = List().empty

    def starlink(slct: Selector): Unit /* List[StarlinkSat] =*/ = slct match {
        case all => {
            val response: String = HttpClient.getStarlink()
            //println("Response:")
            //println(response.toString)
            val parsedJson: Either[io.circe.Error, Json] = parse(response)
            parsedJson match {
                case Right(json) =>
                    val items: List[Json] = json.asArray.getOrElse(Vector.empty).toList
                    println(s"Found ${items.length} items")

                    items.foreach { item =>
                        val itemId = item.hcursor.downField("id").as[String].getOrElse(0)
                        val height = item.hcursor.downField("height_km").as[String].getOrElse("Unknown")
                        //println(s"Item ID: $itemId, Height_km: $height")
                    }

                case Left(error) =>
                println(s"Failed to parse JSON: $error")
            }
        }
        //case active => starlinkSats//.filter(_.active)
        //case inactive => starlinkSats//.filter(!_.active)
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

  def getStarlink(): String = {
    val url = new URL("https://api.spacexdata.com/v4/starlink") // Replace with your API endpoint
    executeRequest(url)
  }

  def getLaunches(): String = {
    val url = new URL("https://api.spacexdata.com/v4/launches") // Replace with your API endpoint
    executeRequest(url)
  }
}
