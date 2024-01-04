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
      |║                                                                                                                   ║
      |╚═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
      |""".stripMargin
  }


  // TUI
  def processInput(input: String): Unit = {
    input match {
      case "d" => println("Show Dashboard.")
                    //Controller Func here
                    showDashboard()
      case "sl" => println("Show Starlink Satalites.")
                    //Controller Func here
      case "la" => println("Show launches.")
                    //Controller Func here
      case "exit" => System.exit(0)              
      case _ => println("Ungültige Eingabe.")
                print(printHeader())
    }
    //print(printHeader())
  }


  def startDashboard(): Unit = {
    var satlist:List[StarlinkSat] = controller.getStarlinkSat()
    
    satlist.foreach { sat =>
      displayResult(sat.name)
    }
  }

  def showDashboard(): Unit = {
    var dashbVals:List[(String, Int)] = controller.getDashboardValues()
    print(printDashboardStarlink())
    dashbVals.foreach { case (listName, count) =>
      println(s"║ $listName, $count")
    }
    print(printDashboardLaunches())
    //TODO Launches
    
  }

  def printDashboardStarlink(): String = {
    """
      |║ ███████ ████████  █████  ██████  ██      ██ ███    ██ ██   ██     
      |║ ██         ██    ██   ██ ██   ██ ██      ██ ████   ██ ██  ██   
      |║ ███████    ██    ███████ ██████  ██      ██ ██ ██  ██ █████    
      |║      ██    ██    ██   ██ ██   ██ ██      ██ ██  ██ ██ ██  ██   
      |║ ███████    ██    ██   ██ ██   ██ ███████ ██ ██   ████ ██   ██ 
      |║ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
      |║ Category, Numbers                                             
      |║+---------------------+
      |""".stripMargin
  }

  def printDashboardLaunches(): String = {
    """
      |║ ██       █████  ██    ██ ███    ██  ██████ ██   ██ ███████ ███████ 
      |║ ██      ██   ██ ██    ██ ████   ██ ██      ██   ██ ██      ██      
      |║ ██      ███████ ██    ██ ██ ██  ██ ██      ███████ █████   ███████ 
      |║ ██      ██   ██ ██    ██ ██  ██ ██ ██      ██   ██ ██           ██ 
      |║ ███████ ██   ██  ██████  ██   ████  ██████ ██   ██ ███████ ███████ 
      |║ - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
      |║ Category, Numbers                                             
      |║+---------------------+
      |""".stripMargin
  }


  def displayResult(result: String): Unit = {
    // Resultii und so
    println(result)
  }

  def getUserInput(): String = {
    print("Eingabe: ")
    scala.io.StdIn.readLine()
  }

  def printExitMessage(): Unit = {
    println("Anwendung wird beendet. Auf Wiedersehen!")
  }


}