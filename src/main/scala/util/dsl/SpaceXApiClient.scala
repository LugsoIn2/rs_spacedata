package SpaceData.util.dsl

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.{HttpURLConnection, URL}



object SpaceXApiClient {

    import SpaceData.util.dsl.Selector

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