package SpaceData.util.dsl

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.{HttpURLConnection, URL}

import SpaceData.util.dsl.SelectorStarlinkSat
import SpaceData.util.dsl.SelectorLaunch


object SpaceXApiClient {

    def executeGetRequest(url: URL): String = {
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

    def executePostRequest(url: URL, payload: String): String = {
        val connection = url.openConnection().asInstanceOf[HttpURLConnection]
        connection.setRequestMethod("POST")
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setDoOutput(true)

        val os: OutputStream = connection.getOutputStream
        os.write(payload.getBytes("utf-8"))
        os.flush()
        var data: String = ""

        val responseCode = connection.getResponseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStreamReader = new InputStreamReader(connection.getInputStream)
            val bufferedReader = new BufferedReader(inputStreamReader)
            var inputLine: String = ""
            val response = new StringBuilder

            while ({inputLine = bufferedReader.readLine(); inputLine != null}) {
                response.append(inputLine.trim)
            }
            bufferedReader.close()

            println(s"Response status: $responseCode")
            //println(s"Response body: ${response.toString}")
            data = response.toString
        } else {
            println(s"Request failed with response code: $responseCode")
        }
        data
    }

  def getStarlink(slct: SelectorStarlinkSat): List[io.circe.Json] = slct match {
    case `all` => {
        val url = new URL("https://api.spacexdata.com/v4/starlink")
        val response: String = executeGetRequest(url)
        SpaceData.util.Helpers.parseToList(response, "get")
    } case `active` => {
        val url = new URL("https://api.spacexdata.com/v4/starlink/query")
        val payload = """{"query":{"spaceTrack.DECAYED":{"$ne":1}}, "options": {"offset": 0, "limit": 100}}""".stripMargin
        val response: String = executePostRequest(url, payload)
        SpaceData.util.Helpers.parseToList(response, "post")
    } case `inactive` => {
        val url = new URL("https://api.spacexdata.com/v4/starlink/query")
        val payload = """{"query":{"spaceTrack.DECAYED":{"$ne":0}}, "options": {"offset": 0, "limit": 100}}""".stripMargin
        val response: String = executePostRequest(url, payload)
        SpaceData.util.Helpers.parseToList(response, "post")
    }
  }

  def getLaunches(slct: SelectorLaunch): List[io.circe.Json] = slct match {
    case `allLaunches` => {
        val url = new URL("https://api.spacexdata.com/v4/launches")
        val response: String = executeGetRequest(url)
        SpaceData.util.Helpers.parseToList(response, "get")
    } case `succeeded` => {
        val url = new URL("https://api.spacexdata.com/v4/launches") // Replace with corresponding API query
        val response: String = executeGetRequest(url)
        SpaceData.util.Helpers.parseToList(response, "get")
    } case `failed` => {
        val url = new URL("https://api.spacexdata.com/v4/launches") // Replace with corresponding API query
        val response: String = executeGetRequest(url)
        SpaceData.util.Helpers.parseToList(response, "get")
    } 
  }
}