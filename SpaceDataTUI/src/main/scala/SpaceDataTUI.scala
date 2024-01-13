package SpaceDataTUI

// SpaceData.scala
import controller.SpaceDataController
import model.StarlinkSat
import view.TUI

object SpaceData extends App {
  // Instances MVC
  val controller = new SpaceDataController()
  val tui = new TUI(controller)

  var userInput = ""
  while (userInput != "exit") {
    userInput = tui.getUserInput()
    tui.processInput(userInput)
  }

}
