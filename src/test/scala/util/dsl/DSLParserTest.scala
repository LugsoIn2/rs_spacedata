import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpecLike
import SpaceData.util.dsl._

class DSLParserSpec extends AnyWordSpecLike {

  "DSLParser" should {

    "parse valid 'show' command" in {
      val input = "show all starlinksat"
      val result = DSLParser.parseCommand(input)

      result shouldBe Some(ShowCommand("all", "starlinksat"))
    }

    "parse valid 'detail' command" in {
      val input = "detail rocket 123"
      val result = DSLParser.parseCommand(input)

      result shouldBe Some(DetailCommand("rocket", "123"))
    }

    "not parse invalid command" in {
      val input = "invalid command"
      val result = DSLParser.parseCommand(input)

      result shouldBe None
    }

    "not parse invalid 'show' command with invalid category" in {
      val input = "show invalid starlinksat"
      val result = DSLParser.parseCommand(input)

      result shouldBe None
    }

    "not parse invalid 'show' command with invalid entity" in {
      val input = "show all invalidentity"
      val result = DSLParser.parseCommand(input)

      result shouldBe None
    }

    "not parse invalid 'detail' command with invalid entity" in {
      val input = "detail invalidentity 123"
      val result = DSLParser.parseCommand(input)

      result shouldBe None
    }
  }
}
