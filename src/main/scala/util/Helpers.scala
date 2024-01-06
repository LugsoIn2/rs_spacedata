package SpaceData.util

import io.circe.parser._
import io.circe._

object  Helpers {
    def parseToList(json: String, method: String): List[io.circe.Json] = {
        var parsedJson: Either[io.circe.Error, Json] = parse(json)

        if (method == "post") {
            // Extract the "docs" array as a List[Json]
            val docsNumber: Either[io.circe.Error, Int] = parsedJson.flatMap(_.hcursor.downField("totalDocs").as[Int])
            println(s"DocsNumber: $docsNumber")
            parsedJson = parsedJson.flatMap(_.hcursor.downField("docs").as[Json])
        }
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
}

