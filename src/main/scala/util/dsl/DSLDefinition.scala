package SpaceData.util.dsl

sealed trait DSLCommand
case class ShowCommand(category: String, entity: String) extends DSLCommand
case class DetailCommand(entity: String, id: String) extends DSLCommand

