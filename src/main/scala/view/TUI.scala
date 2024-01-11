package SpaceData.view
import SpaceData.view.TUIStrings._
import SpaceData.controller.SpaceDataController
import SpaceData.model.{StarlinkSat, Launch, Rocket, SpaceEntity}
import SpaceData.util.dsl.{DSLParser, ShowCommand}
import scala.io.Source

class TUI(var controller:SpaceDataController) extends TUIDSLMode {

  printHeader()

  def processInput(input: String): Unit = {
    input match {
      case "d" => showDashboard()
      case "sl" => showSpaceEntitys("starlinksat")
      case "la" => showLauches()
      case "r" => showSpaceEntitys("rocket")
      case "slid" => showStarlinkSataliteDetails()
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
    printStarlink()
    printDashboardFirstRow()
    dashbStarlinkVals.foreach { case (listName, count) =>
      println(s"║ $listName, $count")
    }
    printLaunches()
    printDashboardFirstRow()
    dashbLaunchVals.foreach { case (listName, count) =>
      println(s"║ $listName, $count")
    }
    printHelpLine()
  }

  def showSpaceEntitys(entity: String): Unit = {
    val slct = scala.io.StdIn.readLine("Options - [all, active, inactive]: ")
    var entitylist: List[SpaceEntity] = Nil
    entity match {
      case "starlinksat" => 
        printStarlink()
        println(s"Satellites in the $slct category are displayed.")
        entitylist = controller.getStarlinkSatList(slct)
      case "rocket" =>
        printRockets()
        println(s"Rockets in the $slct category are displayed.")
        entitylist = controller.getRocketList(slct)  
    }
    printListInChunks(entitylist, (entry: SpaceEntity) => entry.name, (entry: SpaceEntity) => entry.id, 15, "q")
    printHelpLine()
  }

  def showLauches(): Unit = {
    val slct = scala.io.StdIn.readLine("Options - [allLaunches, succeeded, failed]: ")
    printLaunches()
    println(s"Launches in the $slct category are displayed.")
    //TODO:
    var launchlist = controller.getLauchesList(slct)
    printListInChunks(launchlist, (launch: Launch) => launch.name, (launch: Launch) => launch.id, 15, "q")
    printHelpLine()
  }

  def showStarlinkSataliteDetails(): Unit = {
    val id = scala.io.StdIn.readLine("Starlink-Satelite-ID: ")
    printStarlink()
    printDetails()
    println(s"Satellite details with $id are displayed.")
    var satdetails: Option[SpaceEntity] = controller.getStarlinkSatDetails(id)
    val details: String = satdetails.fold("StarlinkSat not found") { starlinkSat => starlinkSat.toString()}
    println(details)
    printHelpLine()
  }

  def showLaucheDetails(): Unit = {
    val id = scala.io.StdIn.readLine("Launch-ID: ")
    printLaunches()
    printDetails()
    println(s"Satellite details with $id are displayed.")
    var launchdetails: Option[Launch]= controller.getLaunchDetails(id)
    val details: String = launchdetails.fold("Launch not found") { launch => launch.toString()}
    println(details)
    printHelpLine()
  }

  def printListInChunks[T](objList: List[T], attribute1Extractor: T => String, attribute2Extractor: T => String, chunkSize: Int, cancelKey: String): Unit = {
    var continuePrinting = true
    objList.grouped(chunkSize).foreach { chunk =>
      if (continuePrinting) {
        chunk.foreach(obj => println(s"Name: ${attribute1Extractor(obj)}, ID: ${attribute2Extractor(obj)}"))
        val userInput = scala.io.StdIn.readLine(s"Press enter for the next page or '$cancelKey' to abort: ")
        if (userInput.toLowerCase == cancelKey.toLowerCase) {
          continuePrinting = false
        }
      }
    }
  }

  def getUserInput(): String = {
    print("Input: ")
    scala.io.StdIn.readLine()
  }

  def printExitMessage(): Unit = {
    println("Progamm exited, bye!")
  }


}