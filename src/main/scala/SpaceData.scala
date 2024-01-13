package SpaceData

// SpaceData.scala
import controller.SpaceDataControllerConsumer
import model.StarlinkSat
import view.TUI

object SpaceData extends App {
  // Instances MVC
  val controller = new SpaceDataControllerConsumer()
  val tui = new TUI(controller)

  var userInput = ""
  while (userInput != "exit") {
    userInput = tui.getUserInput()
    tui.processInput(userInput)
  }

}
