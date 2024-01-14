package SpaceData.model
import SpaceData.model.SpaceEntity


case class Rocket( 
    entityType: String,
    name: String,
    id: String,
    active: Boolean) extends SpaceEntity {
        override def toString: String = s"ID: ${id}\nName: ${name}\n" +
        s"Active: ${active}"
    }

import play.api.libs.json._

object Rocket {
  implicit val rocketFormat: Format[Rocket] = Json.format[Rocket]
}