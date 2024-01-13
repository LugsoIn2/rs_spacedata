package SpaceData

// SpaceData.scala
//import controller.SpaceDataControllerConsumer
import controller.SpaceDataControllerProducer
import model.StarlinkSat
import view.TUI

object SpaceData extends App {
  // Creating and starting a new thread using java.lang.Thread
  val thread = new Thread(new Runnable {
    def run(): Unit = {
      val controllerProducer = new SpaceDataControllerProducer()
      // Code to be executed in the new thread
      controllerProducer.produceSpaceEntitiesList("all", "starlinksat")
      println("all StarlinkSats produced")
      controllerProducer.produceSpaceEntitiesList("active", "starlinksat")
      println("active StarlinkSats produced")
      controllerProducer.produceSpaceEntitiesList("inactive", "starlinksat")
      println("inactive StarlinkSats produced")
      //Thread.sleep(500)
    }
  })

  thread.start()

  // Code in the main thread
  for (i <- 1 to 5) {
    println(s"Main: $i")
    Thread.sleep(500)
  }

  // Wait for the new thread to finish
  thread.join()


  // Instances MVC
  val controller = new SpaceDataControllerConsumer()
  val tui = new TUI(controller)

  var userInput = ""
  while (userInput != "exit") {
    userInput = tui.getUserInput()
    tui.processInput(userInput)
  }

}
