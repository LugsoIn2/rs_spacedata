package SpaceData.model

import SpaceData.model.SpaceEntity
import play.api.libs.json._

case class StarlinkSat(
    entityType: String,
    name: String,
    id: String,
    launchDate: String,
    period: Double,
    height: Double,
    latitude: Double,
    longitude: Double,
    earthRevolutions: Int,
    decayed: Int,
    active: Boolean
) extends SpaceEntity

object StarlinkSat {
  implicit val starlinkSatFormat: Format[StarlinkSat] = Json.format[StarlinkSat]
    def apply(entityType: String, name: String, id: String, launchDate: String, period: Double, height: Double, latitude: Double, longitude: Double, earthRevolutions: Int, decayed: Int): StarlinkSat = {
    val active = decayed != 1
    new StarlinkSat(entityType,name, id, launchDate, period, height, latitude, longitude, earthRevolutions, decayed, active)
  }
}