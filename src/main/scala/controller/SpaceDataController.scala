// SpaceDataController.scala
package SpaceData.controller
import SpaceData.model.StarlinkSat
import SpaceData.controller.SpaceDataStarLinkController
import SpaceData.util.dsl.all
import SpaceData.util.dsl.active
import SpaceData.util.dsl.inactive
import SpaceData.util.dsl.Selector

class SpaceDataController() {

  def getStarlinkSat(): List[StarlinkSat] = {
    //var sat = new StarlinkSat("TestSat1","launchdate-mit-iwas","period", 100, 200.1, 300.1, 580)
    val starlinksatlist = SpaceDataStarLinkController.starlink(all)
    starlinksatlist
  }

  def getCountStarlinkSat(slct: Selector): Int = {
    val starlinksatlist = SpaceDataStarLinkController.starlink(slct)
    starlinksatlist.size
  }

  def getDashboardValues(): List[(String, Int)] = {
    var dashbVals: List[(String, Int)] = List.empty[(String, Int)]
    dashbVals = dashbVals :+ ("all", getCountStarlinkSat(all))
    dashbVals = dashbVals :+ ("active", getCountStarlinkSat(active))
    dashbVals = dashbVals :+ ("inactive", getCountStarlinkSat(inactive))
    dashbVals
  }

}