// SpaceDataController.scala
package SpaceData.controller
import SpaceData.model.StarlinkSat
import SpaceData.controller.SpaceDataStarLinkController
//import SpaceData.util.dsl.all
//import SpaceData.util.dsl.active
//import SpaceData.util.dsl.inactive
//import SpaceData.util.dsl.SelectorStarlinkSat
import SpaceData.util.dsl._
import SpaceData.model.Launch

class SpaceDataController() {
  val starlinksatlist = SpaceDataStarLinkController.starlink(all)
  val starlinksatlistActive = SpaceDataStarLinkController.starlink(active)
  val starlinksatlistInactive = SpaceDataStarLinkController.starlink(inactive)

  val launcheslist = SpaceDataLaunchController.launches(allLaunches)

  def getStarlinkSatList(slct: String): List[StarlinkSat] = {
    val selector = stringToSelecor(slct)
    selector match {
        case `all` => {
          starlinksatlist
        } case `active` => {
          starlinksatlistActive
      } case `inactive` => {
          starlinksatlistInactive
      }
    }
  }

  def getStarlinkSatDetails(id: String): Option[StarlinkSat] = {
    val foundStarlinkSat: Option[StarlinkSat] = findStarlinkSatById(starlinksatlist,id)
    foundStarlinkSat match {
      case Some(starlinkSat) =>
        Some(starlinkSat)
      case None =>
        None
    }
  }

  def findStarlinkSatById(starlinkSats: List[StarlinkSat], targetId: String): Option[StarlinkSat] = {
    starlinkSats.find(_.id == targetId)
  }


  def getLauchesList(slct: String): List[Launch] = {
    launcheslist
  }

  def getLaunchDetails(id: String): Unit = {
    //TODO
    println("TODO:getLaunchDetails(id: String)")
  }

  def getDashboardValues(): List[(String, Int)] = {
    var dashbVals: List[(String, Int)] = List.empty[(String, Int)]
    dashbVals = dashbVals :+ ("all", starlinksatlist.size)
    dashbVals = dashbVals :+ ("active", starlinksatlistActive.size)
    dashbVals = dashbVals :+ ("inactive", starlinksatlistInactive.size)
    dashbVals
  }


  def stringToSelecor(slct: String): SelectorStarlinkSat = {
      //val selector: Selector
      slct.toLowerCase match {
      case "all" => all: SelectorStarlinkSat
      case "active" => active: SelectorStarlinkSat
      case "inactive" => inactive: SelectorStarlinkSat
      //TODO:
      //case "starlink-launch" =>
      //case "id" =>
      case _ => throw new IllegalArgumentException("Ungültiger Selector")
    }
  }

}