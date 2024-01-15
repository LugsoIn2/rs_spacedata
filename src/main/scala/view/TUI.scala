package SpaceData.view
import SpaceData.view.TUIStrings._
// import SpaceData.controller.SpaceDataControllerConsumer
import SpaceData.model.{StarlinkSat, Launch, Rocket, SpaceEntity}
import SpaceData.util.dsl.{DSLParser, ShowCommand}
import scala.io.Source
import SpaceData.controller.SpaceDataController


class TUI(var controller:SpaceDataController) extends TUIDSLMode with TUIHelpers {

  while (
    controller.checkListsNotEmpty()
  ) {
    Thread.sleep(1000)
    println("SpaceData is Loading, please wait...")
  }

  printHeader()

  def processInput(input: String): Unit = {
    input match {
      case "d" => showDashboard()
      case "sl" => 
        printStarlink()
        showSpaceEntitys("starlinksat")
      case "la" => showLauches()
      case "r" => 
        printRockets()
        showSpaceEntitys("rocket")
      case "rid" => 
        printRockets()
        printDetails()
        showSpaceEntityDetails("rocket")
      case "slid" => 
        printStarlink()
        printDetails()
        showSpaceEntityDetails("starlinksat")
      case "laid" => showLaucheDetails()
      case "dsl" => enterDSLMode()  
      case "dslfile" => enterDSLModeFile()
      case "exit" => System.exit(0)
      //case "fu" => mallurge()
      //case "fu2" => controller.consumerLoop()
      case _ =>
        if (input.trim.isEmpty()) {
          printHeader()
        } else {
          println("Incorrect input.")
        }
    }
  }

  // def mallurge(): Unit = {
  //   printListInChunks(controller.rocketsAll, (entry: SpaceEntity) => entry.name, (entry: SpaceEntity) => entry.id, 15, "q")
  // }

  def showDashboard(): Unit = {
    val (dashbStarlinkVals, dashbLaunchVals, dashbRocketsVals) = controller.getDashboardValues()
    def printValues(values: Seq[(String, Int)]): Unit = {
      printDashboardFirstRow()
      values.foreach { case (listName, count) =>
        println(s"║ $listName, $count")
      }
    }
    printStarlink()
    printValues(dashbStarlinkVals)
    printRockets()
    printValues(dashbRocketsVals)
    printLaunches()
    printValues(dashbLaunchVals)
    printHelpLine()
  }

  def showSpaceEntitys(entity: String): Unit = {
    val slct = scala.io.StdIn.readLine("Options - [all, active, inactive]: ")
    println(s"$entity in the $slct category are displayed.")
    val entitylist = controller.getSpaceEntitiesList(slct, entity)
    printListInChunks(entitylist, (entry: SpaceEntity) => entry.name, (entry: SpaceEntity) => entry.id, 15, "q")
    printHelpLine()
  }

  def showSpaceEntityDetails(entity: String): Unit = {
    val id = scala.io.StdIn.readLine("ID: ")
    println(s"$entity details with $id are displayed.")
    val entitydetails: Option[SpaceEntity] = controller.getSpaceEntitiyDetails(id,entity)
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