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
      println("run producer")
      val controllerProducer = new SpaceDataControllerProducer()
      while(Thread.currentThread().isAlive) {
        controllerProducer.producerLoop()
        Thread.sleep(5000)
      }
    }
  })

  thread.start()
  
  // Wait for the new thread to finish
  //thread.join()
  

  println("producerLoop started. Let's start the consumerLoop.")
  val consumer = new SpaceDataControllerProducer()
  consumer.consumerLoop()

  thread.interrupt()

  // Instances MVC
  /*val controller = new SpaceDataControllerConsumer()
  val tui = new TUI(controller)

  var userInput = ""
  while (userInput != "exit") {
    userInput = tui.getUserInput()
    tui.processInput(userInput)
  }*/

}

