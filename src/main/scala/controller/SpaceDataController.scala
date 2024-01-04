// SpaceDataController.scala
package SpaceData.controller
import SpaceData.model.StarlinkSat

class SpaceDataController() {

  def getStarlinkSat(): StarlinkSat = {
    var sat = new StarlinkSat("TestSat1","launchdate-mit-iwas","period", 100, 200.1, 300.1, 580)
    sat
  }

}