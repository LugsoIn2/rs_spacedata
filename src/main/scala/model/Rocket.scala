package SpaceData.model
import play.api.libs.json._

case class Rocket(
    entityType: String,
    name: String,
    id: String,
    active: Boolean
) extends SpaceEntity {
  override def toString: String = s"ID: ${id}\nName: ${name}\n" +
    s"Active: ${active}"
}

object Rocket {
  implicit val rocketFormat: Format[Rocket] = Json.format[Rocket]
}
