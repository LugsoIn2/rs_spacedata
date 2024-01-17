package SpaceData.view

import SpaceData.view.TUIStrings._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class TUIStringsTest extends AnyWordSpec {
  "A TUISTRING" should {
    "not throw an exception when printHeader is called" in {
      noException should be thrownBy printHeader()
    }
    "not throw an exception when printStarlink is called" in {
      noException should be thrownBy(printStarlink())
    }
    "not throw an exception when printRockets is called" in {
      noException should be thrownBy(printRockets())
    }
    "not throw an exception when printDashboardFirstRowis called" in {
      noException should be thrownBy(printDashboardFirstRow())
    }
    "not throw an exception when printDetails is called" in {
      noException should be thrownBy(printDetails())
    }
    "not throw an exception when printHelpLine is called" in {
      noException should be thrownBy(printHelpLine())
    }

  }

}
