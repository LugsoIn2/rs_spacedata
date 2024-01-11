// // DSLHandler.scala
package SpaceData.view

import SpaceData.model.SpaceEntity
import SpaceData.util.dsl.{DSLParser, ShowCommand}
import scala.io.Source
import SpaceData.view.TUIStrings._
trait TUIDSLMode {

  this: TUI =>

  def enterDSLMode(): Unit = {
    val dslCommand = scala.io.StdIn.readLine("Enter DSL command: ")
    executeDSLParser(dslCommand)
    print(printHelpLine())
  }

  def showSpaceEntityDSL(category: String, entity: String): Unit = {
    println(s"$entity in the $category category are displayed.")
    // val entityList: List[SpaceEntity] = entity match {
    //   case "starlinksat" =>
    //     controller.getStarlinkSatList(category)
    //   case "rockets" =>
    //     controller.getRocketList(category)
    //   case _ =>
    //     Nil
    // }
    val entityList: List[SpaceEntity] = controller.getSpaceEntitiesList(category, entity)
    printListInChunks(entityList, (entry: SpaceEntity) => entry.name, (entry: SpaceEntity) => entry.id, 15, "q")
    print(printHelpLine())
  }

  def enterDSLModeFile(): Unit = {
    val defaultPath = "dsl-commands/dslcommands.txt"
    try {
      val filePath = scala.io.StdIn.readLine(s"Enter file path (default: $defaultPath): ").trim match {
        case "" => defaultPath
        case otherPath => otherPath
      }
      val source = Source.fromFile(if (filePath.isEmpty) defaultPath else filePath)
      val commands = source.getLines().toList
      source.close()
      commands.foreach { dslcommand =>
        executeDSLParser(dslcommand)
      }
    } catch {
      case e: Exception =>
        println(s"Error reading or processing DSL commands from file: $e")
    }
    print(printHelpLine())
  }

  def executeDSLParser(dslCommand: String): Unit = {
    println(s"Executing DSL command: $dslCommand")
    DSLParser.parseCommand(dslCommand) match {
      case Some(ShowCommand(category, entity)) =>
        showSpaceEntityDSL(category, entity)
      case None =>
        println("Invalid DSL command.")
    }
  }
}

