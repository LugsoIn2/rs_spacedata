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
) extends SpaceEntity {
    override def toString: String = s"ID: ${id}\nName: ${name}\n" +
          s"Launch Date: ${launchDate}\nPeriod: ${period} minutes\n" +
          s"Height: ${height} km\nLatitude: ${latitude}\nLongitute: ${longitude}\n" +
          s"EarthRevolutions: ${earthRevolutions}\nactive: ${active} "
}


object StarlinkSat {

  def apply(entityType: String, name: String, id: String, launchDate: String, period: Double, height: Double, latitude: Double, longitude: Double, earthRevolutions: Int, decayed: Int): StarlinkSat = {
    val active = decayed != 1
    new StarlinkSat(entityType,name, id, launchDate, period, height, latitude, longitude, earthRevolutions, decayed, active)
  }
  implicit val starlinkSatFormat: Format[StarlinkSat] = new Format[StarlinkSat] {
    override def reads(json: JsValue): JsResult[StarlinkSat] = {
      val entityType = (json \ "entityType").as[String]
      val name = (json \ "name").as[String]
      val id = (json \ "id").as[String]
      val launchDate = (json \ "launchDate").as[String]
      val period = (json \ "period").as[Double]
      val height = (json \ "height").asOpt[Double].getOrElse(0.0) 
      val latitude = (json \ "latitude").asOpt[Double].getOrElse(0.0)
      val longitude = (json \ "longitude").asOpt[Double].getOrElse(0.0)
      val earthRevolutions = (json \ "earthRevolutions").as[Int]
      val decayed = (json \ "decayed").as[Int]

      val active = decayed != 1

      JsSuccess(StarlinkSat(entityType, name, id, launchDate, period, height, latitude, longitude, earthRevolutions, decayed, active))
    }

    override def writes(spaceEntity: StarlinkSat): JsValue = {
      Json.obj(
        "entityType" -> spaceEntity.entityType,
        "name" -> spaceEntity.name,
        "id" -> spaceEntity.id,
        "launchDate" -> spaceEntity.launchDate,
        "period" -> spaceEntity.period,
        "height" -> spaceEntity.height,
        "latitude" -> spaceEntity.latitude,
        "longitude" -> spaceEntity.longitude,
        "earthRevolutions" -> spaceEntity.earthRevolutions,
        "decayed" -> spaceEntity.decayed
      )
    }
  }
}


// object StarlinkSat {
//   implicit val starlinkSatFormat: Format[StarlinkSat] = Json.format[StarlinkSat]
//   def apply(entityType: String, name: String, id: String, launchDate: String, period: Double, height: Double, latitude: Double, longitude: Double, earthRevolutions: Int, decayed: Int): StarlinkSat = {
//     val active = decayed != 1
//     val validheight = Option(height).getOrElse(0.0)
//     val validlatitude = Option(height).getOrElse(0.0)
//     val validlongitude = Option(height).getOrElse(0.0)
//     new StarlinkSat(entityType,name, id, launchDate, period, validheight, validlatitude, validlongitude, earthRevolutions, decayed, active)
//   }
// }



// object StarlinkSat {
//   implicit val starlinkSatFormat: Format[StarlinkSat] = Json.format[StarlinkSat]

//   def apply(
//     entityType: String,
//     name: String,
//     id: String,
//     launchDate: String,
//     period: Double,
//     height: Double,
//     latitude: Double,
//     longitude: Double,
//     earthRevolutions: Int,
//     decayed: Int
//   ): StarlinkSat = {
//     val active = decayed != 1
//     val validHeight = Option(height).getOrElse(0.0)
//     val validLatitude = Option(latitude).getOrElse(0.0)
//     val validLongitude = Option(longitude).getOrElse(0.0)
//     new StarlinkSat(entityType, name, id, launchDate, period, validHeight, validLatitude, validLongitude, earthRevolutions, decayed, active)
//   }
// }

