package SpaceData.view
import SpaceData.controller.SpaceDataController
import SpaceData.model.StarlinkSat

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
      |║                                                                                                                   ║
      |╚═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
      |""".stripMargin
  }


  // TUI
  def processInput(input: String): Unit = {
    input match {
      case "d" => showDashboard()
      case "sl" => showStarlinkSatalites()
      case "la" => showLauches()
      case "slid" => showStarlinkSataliteDetails()
      case "laid" => showLaucheDetails()
      case "exit" => System.exit(0)              
      case _ => if (input.trim.isEmpty()) {
                  print(printHeader())
                } else {
                  println("incorrect input.")
                }
    }
  }

  def showDashboard(): Unit = {
    var dashbVals:List[(String, Int)] = controller.getDashboardValues()
    print(printStarlink())
    print(printDashboardFirstRow())
    dashbVals.foreach { case (listName, count) =>
      println(s"║ $listName, $count")
    }
    print(printLaunches())
    //TODO Launches
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
    var satlist:List[StarlinkSat] = controller.getStarlinkSatList(slct)
    printListInChunks(satlist, (sat: StarlinkSat) => sat.name, (sat: StarlinkSat) => sat.id, 15, "q")
    print(printHelpLine())
  }

  def showLauches(): Unit = {
    val slct = scala.io.StdIn.readLine("Options - [all, starlink-launch]: ")
    print(printLaunches())
    println(s"Launches in the $slct category are displayed.")
    //TODO:
    var launchlist = controller.getLauchesList(slct)
    //printListInChunks(xxxxx)
    println("TODO: Launches List")
    print(printHelpLine())
  }

  def showStarlinkSataliteDetails(): Unit = {
    val id = scala.io.StdIn.readLine("ID: ")
    print(printStarlink())
    print(printDetails())
    println(s"Satellite details with $id are displayed.")
    var satdetails: Option[StarlinkSat] = controller.getStarlinkSatDetails(id)
    val details: String = satdetails.fold("StarlinkSat not found") { starlinkSat =>
    s"ID: ${starlinkSat.id}\nName: ${starlinkSat.name}\nLaunch Date: ${starlinkSat.launchDate}\nPeriod: ${starlinkSat.period}\n" +
      s"Height: ${starlinkSat.height}\nLatitude: ${starlinkSat.latitude}\nLongitute: ${starlinkSat.longitude}\n" +
      s"EarthRevolutions: ${starlinkSat.earthRevolutions}\n"
    }
    println(details)
    print(printHelpLine())
  }

  def showLaucheDetails(): Unit = {
    val id = scala.io.StdIn.readLine("ID: ")
    print(printLaunches())
    print(printDetails())
    println(s"Satellite details with $id are displayed.")
    println("TODO: Launch details with id")
    //TODO  Launch with id
    var launchdetails = controller.getLaunchDetails(id)
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