package SpaceData

// SpaceData.scala
import controller.SpaceDataController
import model.StarlinkSat
import view.TUI

object SpaceData extends App {
  // Instances MVC
  val model = new StarlinkSat("TestSat1","launchdate","period", 100, 200.1, 300.1, 580)
  val tui = new TUI
  val controller = new SpaceDataController(model, tui)

  // Starte die Anwendung über den Controller
  controller.start()
}
