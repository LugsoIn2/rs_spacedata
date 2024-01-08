package SpaceData.view
import SpaceData.controller.SpaceDataController
import SpaceData.model.{StarlinkSat, Launch, Rocket, SpaceEntity}
import SpaceData.util.dsl.{DSLParser, ShowCommand}
import scala.io.Source

class TUI(controller:SpaceDataController) {
  print(printHeader())

  def printHeader() : String = {
    """
      |╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
      |║                  ███████ ██████   █████   ██████ ███████       ██████   █████  ████████  █████                    ║
      |║                  ██      ██   ██ ██   ██ ██      ██            ██   ██ ██   ██    ██    ██   ██                   ║
      |║                  ███████ ██████  ███████ ██      █████   █████ ██   ██ ███████    ██    ███████                   ║
      |║                       ██ ██      ██   ██ ██      ██            ██   ██ ██   ██    ██    ██   ██                   ║
      |║                  ███████ ██      ██   ██  ██████ ███████       ██████  ██   ██    ██    ██   ██                   ║
      |║                                                                                                                   ║
      |║                                      Press "d" to show the Dashboard                                              ║
      |║                                      Press "sl" to show Starlink Satalites                                        ║
      |║                                      Press "la" to show Launches                                                  ║
      |║                                      Press "slid" to show specific Starlink Satalite details via the id           ║
      |║                                      Press "laid" to show specific Launch details via the id                      ║
      |║                                      Type "dsl" to enter DSL mode                                                 ║
      |║                                      Type "dslfile" to enter DSL-File mode                                        ║
      |║                                      Type "exit" to exit the tool                                                 ║
      |║                                                                                                                   ║
      |╚═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
      |""".stripMargin
  }


  def processInput(input: String): Unit = {
    input match {
      case "d" => showDashboard()
      case "sl" => showStarlinkSatalites()
      case "la" => showLauches()
      case "slid" => showStarlinkSataliteDetails()
      case "laid" => showLaucheDetails()
      case "dsl" => enterDSLMode()  
      case "dslfile" => enterDSLModeFile()
      case "exit" => System.exit(0)
      case _ =>
        if (input.trim.isEmpty()) {
          print(printHeader())
        } else {
          println("Incorrect input.")
        }
    }
  }

  // DSLMode Function
  def enterDSLMode(): Unit = {
    val dslCommand = scala.io.StdIn.readLine("Enter DSL command: ")
    DSLParser.parseCommand(dslCommand) match {
      case Some(ShowCommand(category, entity)) =>
        if (entity.toLowerCase == "starlinksat") {
          showStarlinkSatalitesDSL(category)
        } else {
          println("Unsupported entity in DSL command.")
        }
      case None =>
        println("Invalid DSL command.")
    }
    print(printHelpLine())
  }


  def enterDSLModeFile(): Unit = {
    println("Entering DSL mode. Please provide the file path for DSL commands:")
    val filePath = scala.io.StdIn.readLine()
    processCommandsFromFile(filePath)
    print(printHelpLine())
  }


  def processCommandsFromFile(filePath: String): Unit = {
    try {
      val source = Source.fromFile(filePath)
      val commands = source.getLines().toList
      source.close()

      commands.foreach { command =>
        println(s"Executing DSL command: $command")
        DSLParser.parseCommand(command) match {
          case Some(ShowCommand(category, entity)) =>
            if (entity.toLowerCase == "starlinksat") {
              showStarlinkSatalitesDSL(category)
            } else {
              println("Unsupported entity in DSL command.")
            }
          case None =>
            println(s"Invalid DSL command: $command")
        }
      }
    } catch {
      case e: Exception =>
        println(s"Error reading or processing DSL commands from file: $e")
    }
  }


  def showDashboard(): Unit = {
    val (dashbStarlinkVals, dashbLaunchVals) = controller.getDashboardValues()
    print(printStarlink())
    print(printDashboardFirstRow())
    dashbStarlinkVals.foreach { case (listName, count) =>
      println(s"║ $listName, $count")
    }
    print(printLaunches())
    print(printDashboardFirstRow())
    dashbLaunchVals.foreach { case (listName, count) =>
      println(s"║ $listName, $count")
    }
    print(printHelpLine())
  }

  def printStarlink(): String = {
    """
      |║ ███████ ████████  █████  ██████  ██      ██ ███    ██ ██   ██     
      |║ ██         ██    ██   ██ ██   ██ ██      ██ ████   ██ ██  ██   
      |║ ███████    ██    ███████ ██████  ██      ██ ██ ██  ██ █████    
      |║      ██    ██    ██   ██ ██   ██ ██      ██ ██  ██ ██ ██  ██   
      |║ ███████    ██    ██   ██ ██   ██ ███████ ██ ██   ████ ██   ██ 
      |║ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
      |""".stripMargin
  }

  def printDashboardFirstRow(): String = {
    """
      |║ Category, Numbers                                             
      |║+---------------------+
      |""".stripMargin
  }

  def printLaunches(): String = {
    """
      |║ ██       █████  ██    ██ ███    ██  ██████ ██   ██ ███████ ███████ 
      |║ ██      ██   ██ ██    ██ ████   ██ ██      ██   ██ ██      ██      
      |║ ██      ███████ ██    ██ ██ ██  ██ ██      ███████ █████   ███████ 
      |║ ██      ██   ██ ██    ██ ██  ██ ██ ██      ██   ██ ██           ██ 
      |║ ███████ ██   ██  ██████  ██   ████  ██████ ██   ██ ███████ ███████ 
      |║ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
      |""".stripMargin
  }

  def printDetails(): String = {
    """
      |║ ██████  ███████ ████████  █████  ██ ██      ███████ 
      |║ ██   ██ ██         ██    ██   ██ ██ ██      ██      
      |║ ██   ██ █████      ██    ███████ ██ ██      ███████ 
      |║ ██   ██ ██         ██    ██   ██ ██ ██           ██ 
      |║ ██████  ███████    ██    ██   ██ ██ ███████ ███████ 
      |║ - - - - - - - - - - - - - - - - - - - - - - - - - - -
      |""".stripMargin
  }



  def printHelpLine(): String = {
    """Finished: press enter to show menü..."""
  }

  def showStarlinkSatalites(): Unit = {
    val slct = scala.io.StdIn.readLine("Options - [all, active, inactive]: ")
    print(printStarlink())
    println(s"Satellites in the $slct category are displayed.")
    var satlist:List[SpaceEntity] = controller.getStarlinkSatList(slct)
    printListInChunks(satlist, (sat: SpaceEntity) => sat.name, (sat: SpaceEntity) => sat.id, 15, "q")
    print(printHelpLine())
  }

    def showStarlinkSatalitesDSL(slct: String): Unit = {
    print(printStarlink())
    println(s"Satellites in the $slct category are displayed.")
    var satlist:List[SpaceEntity] = controller.getStarlinkSatList(slct)
    printListInChunks(satlist, (sat: SpaceEntity) => sat.name, (sat: SpaceEntity) => sat.id, 15, "q")
    print(printHelpLine())
  }

  def showLauches(): Unit = {
    val slct = scala.io.StdIn.readLine("Options - [allLaunches, succeeded, failed]: ")
    print(printLaunches())
    println(s"Launches in the $slct category are displayed.")
    //TODO:
    var launchlist = controller.getLauchesList(slct)
    printListInChunks(launchlist, (launch: Launch) => launch.name, (launch: Launch) => launch.id, 15, "q")
    print(printHelpLine())
  }

  def showStarlinkSataliteDetails(): Unit = {
    val id = scala.io.StdIn.readLine("Starlink-Satelite-ID: ")
    print(printStarlink())
    print(printDetails())
    println(s"Satellite details with $id are displayed.")
    var satdetails: Option[SpaceEntity] = controller.getStarlinkSatDetails(id)
    val details: String = satdetails.fold("StarlinkSat not found") { starlinkSat => starlinkSat.toString()}
    println(details)
    print(printHelpLine())
  }

  def showLaucheDetails(): Unit = {
    val id = scala.io.StdIn.readLine("Launch-ID: ")
    print(printLaunches())
    print(printDetails())
    println(s"Satellite details with $id are displayed.")
    var launchdetails: Option[Launch]= controller.getLaunchDetails(id)
    val details: String = launchdetails.fold("Launch not found") { launch => launch.toString()}
    println(details)
    print(printHelpLine())
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


  def displayResult(result: String): Unit = {
    // Resultii und so
    println(result)
  }

  def getUserInput(): String = {
    print("Input: ")
    scala.io.StdIn.readLine()
  }

  def printExitMessage(): Unit = {
    println("Progamm exited, bye!")
  }


}