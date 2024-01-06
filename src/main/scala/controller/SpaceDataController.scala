// SpaceDataController.scala
package SpaceData.controller
import SpaceData.model.StarlinkSat
import SpaceData.controller.SpaceDataStarLinkController
import SpaceData.util.spacexApiClient._
import SpaceData.model.Launch

class SpaceDataController() {
  val starlinksatlist = SpaceDataStarLinkController.starlink(all)
  val starlinksatlistActive = SpaceDataStarLinkController.starlink(active)
  val starlinksatlistInactive = SpaceDataStarLinkController.starlink(inactive)

  val launcheslist = SpaceDataLaunchController.launches(allLaunches)

  def getStarlinkSatList(slct: String): List[StarlinkSat] = {
    val selector = stringToSelecorStarlinkSat(slct)
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
    val selector = stringToSelecorLaunch(slct)
    selector match {
        case `allLaunches` => {
          launcheslist
        } case `succeeded` => {
          //TODO
          launcheslist
      } case `failed` => {
          //TODO
          launcheslist
      }
    }
  }

  def getLaunchDetails(id: String): Option[Launch] = {
    val foundLaunch: Option[Launch] = findLaunchById(launcheslist,id)
    foundLaunch match {
      case Some(launch) =>
        Some(launch)
      case None =>
        None
    }
  }

  def findLaunchById(lauches: List[Launch], targetId: String): Option[Launch] = {
    lauches.find(_.id == targetId)
  }

  def getDashboardValues(): (List[(String, Int)],List[(String, Int)]) = {
    var dashbStarlinkVals: List[(String, Int)] = List.empty[(String, Int)]
    var dashbLaunchVals: List[(String, Int)] = List.empty[(String, Int)]
    dashbStarlinkVals = dashbStarlinkVals :+ ("all", starlinksatlist.size)
    dashbStarlinkVals = dashbStarlinkVals :+ ("active", starlinksatlistActive.size)
    dashbStarlinkVals = dashbStarlinkVals :+ ("inactive", starlinksatlistInactive.size)
    dashbLaunchVals = dashbLaunchVals :+ ("allLaunches", launcheslist.size)
    dashbLaunchVals = dashbLaunchVals :+ ("succeeded", launcheslist.size)
    dashbLaunchVals = dashbLaunchVals :+ ("failed", launcheslist.size)
    (dashbStarlinkVals, dashbLaunchVals)
  }


  def stringToSelecorStarlinkSat(slct: String): SelectorStarlinkSat = {
      //val selector: Selector
      slct.toLowerCase match {
      case "all" => all: SelectorStarlinkSat
      case "active" => active: SelectorStarlinkSat
      case "inactive" => inactive: SelectorStarlinkSat
      case _ => throw new IllegalArgumentException("Ungültiger SelectorStarlinkSat")
    }
  }

  def stringToSelecorLaunch(slct: String): SelectorLaunch = {
      //val selector: Selector
      slct match {
      case "allLaunches" => allLaunches: SelectorLaunch
      case "succeeded" => succeeded: SelectorLaunch
      case "failed" => failed: SelectorLaunch
      case _ => throw new IllegalArgumentException("Ungültiger SelectorLaunch")
    }
  }

}