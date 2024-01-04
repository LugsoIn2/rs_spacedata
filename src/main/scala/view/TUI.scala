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
                    showStarlinkSatalites()
      case "la" => println("Show launches.")
                    //Controller Func here
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

  def printHelpLine(): String = {
    """Finished: press enter to show menü..."""
  }


  def showStarlinkSatalites(): Unit = {
    val slct = scala.io.StdIn.readLine("Options - [all, active, inactive]: ")
    println(s"Satellites in the $slct category are displayed.")
    print(printStarlink())
    var satlist:List[StarlinkSat] = controller.getStarlinkSatList(slct)
    printListInChunks(satlist, (sat: StarlinkSat) => sat.name, 15, "q")
    print(printHelpLine())
  }

  def showLauches(): Unit = {
    print("TODO: Launches List")
    print(printHelpLine())
  }


// def printListInChunks[T](objList: List[T], attributeExtractor: T => String, chunkSize: Int): Unit = {
//   objList.grouped(chunkSize).foreach { chunk =>
//     chunk.foreach(obj => println(attributeExtractor(obj)))
//     scala.io.StdIn.readLine("press enter for the next page...")
//   }
// }

def printListInChunks[T](objList: List[T], attributeExtractor: T => String, chunkSize: Int, cancelKey: String): Unit = {
  var continuePrinting = true

  objList.grouped(chunkSize).foreach { chunk =>
    if (continuePrinting) {
      chunk.foreach(obj => println(attributeExtractor(obj)))
      
      val userInput = scala.io.StdIn.readLine(s"press enter for the next page or '$cancelKey' to abort: ")
      
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