package SpaceData.model

case class StarlinkSat( 
    name: String,
    id: String,
    launchDate: String,
    period: String,
    height: Int,
    latitude: Double,
    longitude: Double,
    earthRevolutions: Int) { 
        override def toString: String = s"ID: ${id}\nName: ${name}\n" +
          s"Launch Date: ${launchDate}\nPeriod: ${period}\n" +
          s"Height: ${height}\nLatitude: ${latitude}\nLongitute: ${longitude}\n" +
          s"EarthRevolutions: ${earthRevolutions}\n"

    }

// class SpaceDataModel {
//   def processApiData(data: StarlinkSat): String = {
//     // API DATA
//     s"Example idk: ${data.name}"
//   }

// }