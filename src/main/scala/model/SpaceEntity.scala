package SpaceData.model

import play.api.libs.json._

trait SpaceEntity {
  def entityType: String
  def name: String
  def id: String
}

object SpaceEntity {
  implicit val spaceEntityFormat: Format[SpaceEntity] = new Format[SpaceEntity] {
    override def reads(json: JsValue): JsResult[SpaceEntity] = {
      (json \ "entityType").asOpt[String] match {
        case Some("Rocket")      => json.validate[Rocket]
        case Some("StarlinkSat") => json.validate[StarlinkSat]
        case Some(entityType)    => JsError(s"Unknown entity type: $entityType")
        case None                => JsError("Missing entityType field")
      }
    }

    override def writes(spaceEntity: SpaceEntity): JsValue = {
      spaceEntity match {
        case rocket: Rocket        => Json.toJson(rocket)(Rocket.rocketFormat)
        case starlinkSat: StarlinkSat => Json.toJson(starlinkSat)(StarlinkSat.starlinkSatFormat)
      }
    }
  }
}