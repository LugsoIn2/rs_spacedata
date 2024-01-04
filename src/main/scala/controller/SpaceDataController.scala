// SpaceDataController.scala
package SpaceData.controller
import SpaceData.model.StarlinkSat
import SpaceData.controller.SpaceDataStarLinkController
import SpaceData.util.dsl.all
import SpaceData.util.dsl.active
import SpaceData.util.dsl.inactive

class SpaceDataController() {

  def getStarlinkSat(): List[StarlinkSat] = {
    //var sat = new StarlinkSat("TestSat1","launchdate-mit-iwas","period", 100, 200.1, 300.1, 580)
    val starlinksatlist = SpaceDataStarLinkController.starlink(all)
    starlinksatlist
  }

}