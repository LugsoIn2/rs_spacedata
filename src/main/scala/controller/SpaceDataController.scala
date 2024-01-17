package SpaceData.controller

import SpaceData.model.{StarlinkSat, Launch, Rocket, SpaceEntity}
import SpaceData.controller.{active, inactive, all}
import SpaceData.controller.kafka.SpaceDataConsumer

class SpaceDataController() {
  val consumerController = new SpaceDataConsumer()

  def checkListsNotEmpty(): Boolean = {
    consumerController.rocketslistAll.isEmpty &&
    consumerController.rocketslisActive.isEmpty &&
    consumerController.rocketslisInactive.isEmpty &&
    consumerController.starlinksatlistAll.isEmpty &&
    consumerController.starlinksatlistActive.isEmpty &&
    consumerController.starlinksatlistInactive.isEmpty
    false
  }
  
  def getStarlinkSpeedList(): List[StarlinkSat] = {
    val starlinksatlistSpeed = consumerController.consumeFromKafkaWithSpark("starlinksats-active")
    starlinksatlistSpeed
  }

  def getSpaceEntitiesList(slct: String, entity: String): List[SpaceEntity] = {
    val selector = stringToSelecorSpaceEntity(slct)
    val EntityList = entity match {
      case "starlinksat" => getStarlinkList(selector)
      case "rocket" => getRocketList(selector)
      case _ => throw new IllegalArgumentException(s"Unsupported entity type: $entity")
    }
    EntityList
  }


  def getRocketList(selector: SelectorSpaceEntity): List[SpaceEntity] = {
    val result: List[SpaceEntity] = selector match {
      case `all` =>
        // consumerController.consumeFromKafkaWithSpark2("rockets-all")
        // consumerController.consumeFromKafkaWithSpark("rockets-all")
        consumerController.rocketslistAll
      case `active` =>
        consumerController.rocketslisActive
      case `inactive` =>
        consumerController.rocketslisInactive
    }
    result
  }

  def getStarlinkList(selector: SelectorSpaceEntity): List[SpaceEntity] = {

    val result: List[SpaceEntity] = selector match {
      case `all` =>
        consumerController.starlinksatlistAll
      case `active` =>
        consumerController.starlinksatlistActive
      case `inactive` =>
        consumerController.starlinksatlistInactive
    }
    result
  }


  def getSpaceEntitiyDetails(id: String, entity: String): Option[SpaceEntity] = {
    val starlinksatlist = getSpaceEntitiesList("all", entity: String)
    val foundEntitiy: Option[SpaceEntity] = findStarlinkSatById(starlinksatlist,id)
    foundEntitiy match {
      case Some(entry) =>
        Some(entry)
      case None =>
        None
    }
  }

  def findStarlinkSatById(entity: List[SpaceEntity], targetId: String): Option[SpaceEntity] = {
    entity.find(_.id == targetId)
  }

  def getDashboardValues(): (List[(String, Int)], List[(String, Int)]) = {
    val dashbStarlinkVals: List[(String, Int)] =
      List(
        ("all", getSpaceEntitiesList("all", "starlinksat").size),
        ("active", getSpaceEntitiesList("active", "starlinksat").size),
        ("inactive", getSpaceEntitiesList("inactive", "starlinksat").size)
      )
    val dashbRocketsVals: List[(String, Int)] =
      List(
        ("all", getSpaceEntitiesList("all", "rocket").size),
        ("active", getSpaceEntitiesList("active", "rocket").size),
        ("inactive", getSpaceEntitiesList("inactive", "rocket").size)
      )
    (dashbStarlinkVals,  dashbRocketsVals)
  }

  def stringToSelecorSpaceEntity(slct: String): SelectorSpaceEntity = {
      slct.toLowerCase match {
      case "all" => all: SelectorSpaceEntity
      case "active" => active: SelectorSpaceEntity
      case "inactive" => inactive: SelectorSpaceEntity
      case _ => throw new IllegalArgumentException("Ungültiger SelectorSpaceEntity")
    }
  }

}