// SpaceDataController.scala
package SpaceData.controller
import SpaceData.model.StarlinkSat
import SpaceData.controller.SpaceDataStarLinkController
import SpaceData.util.dsl.all
import SpaceData.util.dsl.active
import SpaceData.util.dsl.inactive
import SpaceData.util.dsl.Selector

class SpaceDataController() {

  def getStarlinkSatList(slct: String): List[StarlinkSat] = {
    val starlinksatlist = SpaceDataStarLinkController.starlink(stringToSelecor(slct))
    starlinksatlist
  }

  def getCountStarlinkSat(slct: String): Int = {
    val starlinksatlist = getStarlinkSatList(slct)
    starlinksatlist.size
  }

  def getLauchesList(slct: String): Unit = {
    //TODO
    print("TODO:Launches")
  }

  def getDashboardValues(): List[(String, Int)] = {
    var dashbVals: List[(String, Int)] = List.empty[(String, Int)]
    dashbVals = dashbVals :+ ("all", getCountStarlinkSat("all"))
    dashbVals = dashbVals :+ ("active", getCountStarlinkSat("active"))
    dashbVals = dashbVals :+ ("inactive", getCountStarlinkSat("inactive"))
    dashbVals
  }


  def stringToSelecor(slct: String): Selector = {
      //val selector: Selector
      slct.toLowerCase match {
      case "all" => all: Selector
      case "active" => active: Selector
      case "inactive" => inactive: Selector
      case _ => throw new IllegalArgumentException("Ungültiger Selector")
    }
  }

}