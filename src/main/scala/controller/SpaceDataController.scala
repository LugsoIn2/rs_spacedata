// SpaceDataController.scala
package SpaceData.controller

import SpaceData.model.StarlinkSat
import SpaceData.view.TUI

class SpaceDataController(model: StarlinkSat, tui: TUI) {

  def start(): Unit = {


    // Controller to view Header
    tui.printHeader()

    // User input
    val userInput = scala.io.StdIn.readLine()
    //val result = processUserInput(userInput)

    // Controller to view
    //tui.displayResult(result)
  }

}
