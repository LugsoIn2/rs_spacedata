package SpaceData.model

case class StarlinkSat( 
    name: String,
    launchDate: String,
    period: String,
    height: Int,
    latitude: Double,
    longitude: Double,
    earthRevolutions: Int) { }

// class SpaceDataModel {
//   def processApiData(data: StarlinkSat): String = {
//     // API DATA
//     s"Example idk: ${data.name}"
//   }

// }