package SpaceDataTUI.model

import SpaceDataTUI.model.SpaceEntity

case class StarlinkSat( 
    name: String,
    id: String,
    launchDate: String,
    period: Double,
    height: Double,
    latitude: Double,
    longitude: Double,
    earthRevolutions: Int,
    decayed: Int,
    active: Boolean) extends SpaceEntity { 
        override def toString: String = s"ID: ${id}\nName: ${name}\n" +
          s"Launch Date: ${launchDate}\nPeriod: ${period} minutes\n" +
          s"Height: ${height} km\nLatitude: ${latitude}\nLongitute: ${longitude}\n" +
          s"EarthRevolutions: ${earthRevolutions}\nactive: ${active} "

    }

object StarlinkSat {
  def apply(name: String, id: String, launchDate: String, period: Double, height: Double, latitude: Double, longitude: Double, earthRevolutions: Int, decayed: Int): StarlinkSat = {
    val active = decayed != 1
    new StarlinkSat(name, id, launchDate, period, height, latitude, longitude, earthRevolutions, decayed, active)
  }
}