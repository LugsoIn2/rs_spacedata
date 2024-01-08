package SpaceData.model

import SpaceData.model.SpaceEntity

case class StarlinkSat( 
    name: String,
    id: String,
    launchDate: String,
    period: Double,
    height: Double,
    latitude: Double,
    longitude: Double,
    earthRevolutions: Int) extends SpaceEntity { 
        override def toString: String = s"ID: ${id}\nName: ${name}\n" +
          s"Launch Date: ${launchDate}\nPeriod: ${period} minutes\n" +
          s"Height: ${height} km\nLatitude: ${latitude}\nLongitute: ${longitude}\n" +
          s"EarthRevolutions: ${earthRevolutions}\n"

    }

// class SpaceDataModel {
//   def processApiData(data: StarlinkSat): String = {
//     // API DATA
//     s"Example idk: ${data.name}"
//   }

// }