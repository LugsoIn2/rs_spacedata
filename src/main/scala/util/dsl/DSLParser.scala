package SpaceData.util.dsl


object DSLParser {
  def parseCommand(input: String): Option[DSLCommand] = {
    val tokens = input.trim.toLowerCase.split("\\s+").toList

    tokens match {
      case "show" :: category :: entity :: Nil if isValidCategory(category) && isValidEntity(entity) =>
        Some(ShowCommand(category, entity))
      case _ =>
        None
    }
  }

  private def isValidCategory(category: String): Boolean =
    List("all", "active", "inactive").contains(category)

  private def isValidEntity(entity: String): Boolean =
    entity.toLowerCase == "starlinksat"
}
