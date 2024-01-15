// SpaceDataController.scala
package SpaceData.controller
import SpaceData.model.{StarlinkSat, Launch, Rocket, SpaceEntity}
import SpaceData.controller.{active, inactive, all}
import SpaceData.util.spacexApiClient._
import SpaceData.controller.kafka.SpaceDataConsumer

class SpaceDataController() {
  val consumerController = new SpaceDataConsumer()
  // val launcheslist = SpaceDataLaunchController.launches(allLaunches)

  def checkListsNotEmpty(): Boolean = {
    consumerController.rocketslistAll.isEmpty &&
    consumerController.rocketslisActive.isEmpty &&
    consumerController.rocketslisInactive.isEmpty &&
    consumerController.starlinksatlistAll.isEmpty &&
    consumerController.starlinksatlistActive.isEmpty &&
    consumerController.starlinksatlistInactive.isEmpty
    false
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
        consumerController.consumeFromKafkaWithSpark("rockets-all")
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


  // def getLauchesList(slct: String): List[Launch] = {
  //   val selector = stringToSelecorLaunch(slct)
  //   selector match {
  //       case `allLaunches` => {
  //         launcheslist
  //       } case `succeeded` => {
  //         //TODO
  //         launcheslist
  //     } case `failed` => {
  //         //TODO
  //         launcheslist
  //     }
  //   }
  // }

  // def getLaunchDetails(id: String): Option[Launch] = {
  //   val foundLaunch: Option[Launch] = findLaunchById(launcheslist,id)
  //   foundLaunch match {
  //     case Some(launch) =>
  //       Some(launch)
  //     case None =>
  //       None
  //   }
  // }

  def findLaunchById(lauches: List[Launch], targetId: String): Option[Launch] = {
    lauches.find(_.id == targetId)
  }

  def getDashboardValues(): (List[(String, Int)], /* List[(String, Int)], */ List[(String, Int)]) = {
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
    // val dashbLaunchVals: List[(String, Int)] =
    //   List(
    //     ("allLaunches", launcheslist.size),
    //     ("succeeded", launcheslist.size),
    //     ("failed", launcheslist.size)
    //   )
    (dashbStarlinkVals, /* dashbLaunchVals, */ dashbRocketsVals)
  }

  def stringToSelecorSpaceEntity(slct: String): SelectorSpaceEntity = {
      //val selector: Selector
      slct.toLowerCase match {
      case "all" => all: SelectorSpaceEntity
      case "active" => active: SelectorSpaceEntity
      case "inactive" => inactive: SelectorSpaceEntity
      case _ => throw new IllegalArgumentException("Ungültiger SelectorSpaceEntity")
    }
  }

  def stringToSelecorLaunch(slct: String): SelectorLaunch = {
      //val selector: Selector
      slct match {
      case "allLaunches" => allLaunches: SelectorLaunch
      case "succeeded" => succeeded: SelectorLaunch
      case "failed" => failed: SelectorLaunch
      case _ => throw new IllegalArgumentException("Ungültiger SelectorLaunch")
    }
  }

}