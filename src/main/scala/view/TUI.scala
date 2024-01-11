package SpaceData.view
import SpaceData.view.TUIStrings._
import SpaceData.controller.SpaceDataController
import SpaceData.model.{StarlinkSat, Launch, Rocket, SpaceEntity}
import SpaceData.util.dsl.{DSLParser, ShowCommand}
import scala.io.Source

class TUI(var controller:SpaceDataController) extends TUIDSLMode with TUIHelpers {

  printHeader()

  def processInput(input: String): Unit = {
    input match {
      case "d" => showDashboard()
      case "sl" => showSpaceEntitys("starlinksat")
      case "la" => showLauches()
      case "r" => showSpaceEntitys("rocket")
      case "slid" => showSpaceEntityDetails("starlinksat")
      case "laid" => showLaucheDetails()
      case "dsl" => enterDSLMode()  
      case "dslfile" => enterDSLModeFile()
      case "exit" => System.exit(0)
      case _ =>
        if (input.trim.isEmpty()) {
          printHeader()
        } else {
          println("Incorrect input.")
        }
    }
  }

  def showDashboard(): Unit = {
    val (dashbStarlinkVals, dashbLaunchVals) = controller.getDashboardValues()
    def printValues(values: Seq[(String, Int)]): Unit = {
      printDashboardFirstRow()
      values.foreach { case (listName, count) =>
        println(s"║ $listName, $count")
      }
    }
    printStarlink()
    printValues(dashbStarlinkVals)
    printLaunches()
    printValues(dashbLaunchVals)
    printHelpLine()
  }

  def showSpaceEntitys(entity: String): Unit = {
    val slct = scala.io.StdIn.readLine("Options - [all, active, inactive]: ")
    println(s"$entity in the $slct category are displayed.")
    val entitylist: List[SpaceEntity] = entity match {
      case "starlinksat" =>
        printStarlink()
        controller.getStarlinkSatList(slct)
      case "rocket" =>
        printRockets()
        controller.getRocketList(slct)
      case _ =>
        Nil // default case, an empty list
    }
    printListInChunks(entitylist, (entry: SpaceEntity) => entry.name, (entry: SpaceEntity) => entry.id, 15, "q")
    printHelpLine()
  }

  def showSpaceEntityDetails(entity: String): Unit = {
    val id = scala.io.StdIn.readLine("ID: ")
    val entitydetails: Option[SpaceEntity] = entity match {
      case "starlinksat" =>
        printStarlink()
        printDetails()
        println(s"Satellite details with $id are displayed.")
        controller.getStarlinkSatDetails(id)
      case "rocket" =>
        printRockets()
        //TODO ROCKET DETAILS
        //controller.getStarlinkSatDetails(id)
        None
      case _ =>
        None 
    }
    println(entitydetails.fold(s"$entity not found") { entry => entry.toString()})
    printHelpLine()
  }

  def showLauches(): Unit = {
    val slct = scala.io.StdIn.readLine("Options - [allLaunches, succeeded, failed]: ")
    printLaunches()
    println(s"Launches in the $slct category are displayed.")
    val launchlist = controller.getLauchesList(slct)
    printListInChunks(launchlist, (launch: Launch) => launch.name, (launch: Launch) => launch.id, 15, "q")
    printHelpLine()
  }

  def showLaucheDetails(): Unit = {
    val id = scala.io.StdIn.readLine("Launch-ID: ")
    printLaunches()
    printDetails()
    println(s"Satellite details with $id are displayed.")
    val launchdetails: Option[Launch] = controller.getLaunchDetails(id)
    val details: String = launchdetails.fold("Launch not found") { launch =>
      launch.toString()
    }
    println(details)
    printHelpLine()
  }

}