package SpaceData.controller.actor

import SpaceData.model.{SpaceEntity}
import SpaceData.controller.factories.{StarlinkSatFactory, RocketFactory}
import io.circe.Json
import io.circe.parser._
import io.circe._

object HttpClientActorHelpers {
  def createSpaceEntitiesInstances(endpoint: String, body: String): List[SpaceEntity] = {
    val entityList: List[SpaceEntity] = endpoint match {
      case "/starlink" => parseToList(body).map(item => StarlinkSatFactory.createInstance(item))
      case "/rockets"  => parseToList(body).map(item => RocketFactory.createInstance(item))
      case _           => List.empty
    }
    entityList
  }

  def parseToList(json: String): List[io.circe.Json] = {
    parse(json) match {
      case Right(json) => json.asArray.getOrElse(Vector.empty).toList
      case Left(error) => List.empty
    }
  }
}