// SpaceDataController.scala
package SpaceData.controller
import SpaceData.model.StarlinkSat
import SpaceData.controller.SpaceDataStarLinkController
import SpaceData.util.dsl.all
import SpaceData.util.dsl.active
import SpaceData.util.dsl.inactive
import SpaceData.util.dsl.Selector

class SpaceDataController2() {
  val starlinksatlist = SpaceDataStarLinkController.starlink(stringToSelecor("all"))

  def getStarlinkSatList(slct: String): List[StarlinkSat] = {
    starlinksatlist
  }

  def getCountStarlinkSat(slct: String): Int = {
    val starlinksatlist = getStarlinkSatList(slct)
    starlinksatlist.size
  }

  def getStarlinkSatDetails(id: String): Option[StarlinkSat] = {
    val foundStarlinkSat: Option[StarlinkSat] = findStarlinkSatById(starlinksatlist,id)
    foundStarlinkSat match {
      case Some(starlinkSat) =>
        //val starlinkSatDetails:  = ()
        Some(starlinkSat)
      case None =>
        None
    }
  }

  def findStarlinkSatById(starlinkSats: List[StarlinkSat], targetId: String): Option[StarlinkSat] = {
    starlinkSats.find(_.id == targetId)
  }


  def getLauchesList(slct: String): Unit = {
    //TODO
    println("TODO:getLauchesList(slct: String)")
  }

  def getCountLaunches(slct: String): Unit = {
    //TODO
    println("TODO:getCountLaunches(slct: String)")
  }

  def getLaunchDetails(id: String): Unit = {
    //TODO
    println("TODO:getLaunchDetails(id: String)")
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
      //TODO:
      //case "starlink-launch" =>
      //case "id" =>
      case _ => throw new IllegalArgumentException("Ungültiger Selector")
    }
  }

}