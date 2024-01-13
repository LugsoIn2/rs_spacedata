// // DSLHandler.scala
package SpaceDataTUI.view

import SpaceDataTUI.model.SpaceEntity
import SpaceDataTUI.util.dsl.{DSLParser, ShowCommand}
import scala.io.Source
import SpaceDataTUI.view.TUIStrings._
import SpaceDataTUI.util.dsl.DetailCommand
trait TUIDSLMode {

  this: TUI =>

  def enterDSLMode(): Unit = {
    val dslCommand = scala.io.StdIn.readLine("Enter DSL command: ")
    executeDSLParser(dslCommand)
    printHelpLine()
  }

  def showSpaceEntityDSL(category: String, entity: String): Unit = {
    printDSLHeaders(entity, false)
    println(s"$entity in the $category category are displayed.")
    val entityList: List[SpaceEntity] = controller.getSpaceEntitiesList(category, entity)
    printListInChunks(entityList, (entry: SpaceEntity) => entry.name, (entry: SpaceEntity) => entry.id, 15, "q")
    printHelpLine()
  }

  def DetailSpaceEntityDSL(entity: String, id: String): Unit = {
    printDSLHeaders(entity, true)
    println(s"$entity with id $id Details are displayed.")
    val entitydetails: Option[SpaceEntity] = controller.getSpaceEntitiyDetails(id, entity)
    println(entitydetails.fold(s"$entity not found") { entry => entry.toString()})
    printHelpLine()
  }

  def printDSLHeaders(entity: String, details: Boolean): Unit = {
    entity match {
      case "starlinksat" => printStarlink()
      case "rocket" => printRockets()
    }
    if (details) {printDetails()}
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
    printHelpLine()
  }

  def executeDSLParser(dslCommand: String): Unit = {
    println(s"Executing DSL command: $dslCommand")
    DSLParser.parseCommand(dslCommand) match {
      case Some(ShowCommand(category, entity)) =>
        showSpaceEntityDSL(category, entity)
      case Some(DetailCommand(entity, id)) =>
        DetailSpaceEntityDSL(entity,id)
      case None =>
        println("Invalid DSL command.")
    }
  }
}

