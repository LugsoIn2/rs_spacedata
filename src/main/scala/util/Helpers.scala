package SpaceData.util

import io.circe.parser._
import io.circe._

object  Helpers {
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
}

