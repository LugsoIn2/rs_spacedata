// SpaceDataController.scala
package SpaceData.controller

import SpaceData.model.StarlinkSat
import SpaceData.view.TUI

class SpaceDataController(model: StarlinkSat, tui: TUI) {

  def start(): Unit = {


    var exitCommand = false
    var userInput = ""

    while (!exitCommand) {
      userInput = tui.getUserInput()

      // Überprüfe auf Exit-Befehl
      if (userInput.toLowerCase == "exit") {
        exitCommand = true
      } else {
        // Verarbeite Benutzereingabe über das Modell
        //val result = processUserInput(spaceData, userInput)

        // Hier sollte der Controller die Ansicht über das Ergebnis informieren
        //tui.displayResult(result)
      }

    }





    // Controller to view Header
    ////tui.printHeader()

    // User input
    ////val userInput = scala.io.StdIn.readLine()
    //val result = processUserInput(userInput)

    // Controller to view
    //tui.displayResult(result)
  }

}
