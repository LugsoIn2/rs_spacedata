package SpaceData

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import controller.SpaceDataController
import controller.SpaceDataControllerProducer
import view.TUI

object SpaceData extends App {

  // Creating and starting a new Future for the producer
  val producerFuture: Future[Unit] = Future {
    println("run producer")
    val controllerProducer = new SpaceDataControllerProducer()
    while (true) {
      controllerProducer.produceEntityToKafka()
      Thread.sleep(5000)
    }
  }

  // Instances MVC
  val controller = new SpaceDataController()
  val tui = new TUI(controller)

  var userInput = ""
  while (userInput != "exit") {
    userInput = tui.getUserInput()
    tui.processInput(userInput)
  }

  // Interrupting the producer Future
  producerFuture.onComplete(_ => println("Producer Future completed"))
  producerFuture.foreach(_ => ())
  Await.result(producerFuture, Duration.Inf)

}


