package SpaceData

import controller.SpaceDataController
import view.TUI

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._

class SpaceDataIntegrationSpec extends AnyWordSpec with Matchers with MockitoSugar {

  "SpaceData application" should {
    "correctly process user input until 'exit'" in {
      val mockedController = mock[SpaceDataController]
      val mockedTUI = mock[TUI]

      // Simulate user input and expected behaviors
      when(mockedTUI.getUserInput()).thenReturn("sl", "exit")

      val spaceData = new SpaceData(mockedController, mockedTUI)
      spaceData.run()

      // Verify that getUserInput and processInput were called as expected
      verify(mockedTUI, times(2)).getUserInput()
      verify(mockedTUI, times(1)).processInput("sl")
      verify(mockedTUI, times(1)).processInput("exit")
    }

    // Add more integration tests for different user input scenarios

    // Make sure to test edge cases and handle different scenarios
  }

  class SpaceData(controller: SpaceDataController, tui: TUI) {
    def run(): Unit = {
      var userInput = ""
      while (userInput != "exit") {
        userInput = tui.getUserInput()
        tui.processInput(userInput)
      }
    }
  }
}
