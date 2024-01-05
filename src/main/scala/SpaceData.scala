package SpaceData

// SpaceData.scala
import controller.SpaceDataController
import controller.SpaceDataController2
import model.StarlinkSat
import view.TUI

object SpaceData extends App {
  // Instances MVC
  val controller = new SpaceDataController2()
  val tui = new TUI(controller)

  var userInput = ""
  while (userInput != "exit") {
    userInput = tui.getUserInput()
    tui.processInput(userInput)
  }

}
