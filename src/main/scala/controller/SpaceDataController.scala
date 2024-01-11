// SpaceDataController.scala
package SpaceData.controller
import SpaceData.model.{StarlinkSat, Launch, Rocket, SpaceEntity}
import SpaceData.controller.{active, inactive, all}
import SpaceData.util.spacexApiClient._
import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import akka.pattern.ask
import scala.concurrent.Future
import javax.xml.crypto.Data
import akka.util.Timeout
import scala.concurrent.ExecutionContextExecutor
import akka.actor.ActorRef

class SpaceDataController() {
  // Actor System
  implicit val httpActorSystem: ActorSystem = ActorSystem("HttpActorSystem")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = httpActorSystem.dispatcher
  
  // create Actors
  val httpClientActorStarlinkSats = httpActorSystem.actorOf(Props(new HttpClientActor))
  val httpClientActorRockets = httpActorSystem.actorOf(Props(new HttpClientActor))

  // get data from API
  httpClientActorStarlinkSats ! GetSpaceEntities("/starlink")
  httpClientActorRockets ! GetSpaceEntities("/rockets")

  //ar starlinksatlist = SpaceDataStarLinkController.starlink(all)
  // val starlinksatlistActive = SpaceDataStarLinkController.starlink(active)
  // val starlinksatlistInactive = SpaceDataStarLinkController.starlink(inactive)

  val launcheslist = SpaceDataLaunchController.launches(allLaunches)

  def getSpaceEntitiesList(slct: String, entity: String): List[SpaceEntity] = {
    val selector = stringToSelecorSpaceEntity(slct)
    implicit val timeout: Timeout = Timeout(10.seconds)
    val httpClientActor = entity match {
      case "starlinksat" => httpClientActorStarlinkSats
      case "rocket" => httpClientActorRockets
      case _ => throw new IllegalArgumentException(s"Unsupported entity type: $entity")
    }

    val result: List[SpaceEntity] = selector match {
      case `all` =>
        val futureEntities: Future[List[SpaceEntity]] = (httpClientActor ? GetCurrentState)
          .mapTo[List[SpaceEntity]]
          .recover { case _ => Nil }
        Await.result(futureEntities, 10.seconds)

      case `active` =>
        Await.result(getAndFilterEntites(true, httpClientActor, entity), 10.seconds)

      case `inactive` =>
        Await.result(getAndFilterEntites(false, httpClientActor, entity), 10.seconds)
    }

    result
  }

  def getAndFilterEntites(isActive: Boolean, httpClientActor: ActorRef, entityType: String): Future[List[SpaceEntity]] = {
    implicit val timeout: Timeout = Timeout(10.seconds)
    Source.single(())
      .mapAsync(1)(_ => httpClientActor ? GetCurrentState)
      .map { data =>
        val instances = data.asInstanceOf[List[SpaceEntity]]
        entityType match {
          case "starlinksat" =>
            if (isActive) instances.collect { case sat: StarlinkSat if sat.active => sat }
            else instances.collect { case sat: StarlinkSat if !sat.active => sat }

          case "rocket" =>
            if (isActive) instances.collect { case rocket: Rocket if rocket.active => rocket }
            else instances.collect { case rocket: Rocket if !rocket.active => rocket }

          case _ => Nil // Handle other types or provide a default case
        }
      }
      .runWith(Sink.seq)
      .map(_.flatten.toList)
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


  def getLauchesList(slct: String): List[Launch] = {
    val selector = stringToSelecorLaunch(slct)
    selector match {
        case `allLaunches` => {
          launcheslist
        } case `succeeded` => {
          //TODO
          launcheslist
      } case `failed` => {
          //TODO
          launcheslist
      }
    }
  }

  def getLaunchDetails(id: String): Option[Launch] = {
    val foundLaunch: Option[Launch] = findLaunchById(launcheslist,id)
    foundLaunch match {
      case Some(launch) =>
        Some(launch)
      case None =>
        None
    }
  }

  def findLaunchById(lauches: List[Launch], targetId: String): Option[Launch] = {
    lauches.find(_.id == targetId)
  }

  def getDashboardValues(): (List[(String, Int)], List[(String, Int)], List[(String, Int)]) = {
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
    val dashbLaunchVals: List[(String, Int)] =
      List(
        ("allLaunches", launcheslist.size),
        ("succeeded", launcheslist.size),
        ("failed", launcheslist.size)
      )
    (dashbStarlinkVals, dashbLaunchVals, dashbRocketsVals)
  }

  def stringToSelecorSpaceEntity(slct: String): SelectorSpaceEntity = {
      //val selector: Selector
      slct.toLowerCase match {
      case "all" => all: SelectorSpaceEntity
      case "active" => active: SelectorSpaceEntity
      case "inactive" => inactive: SelectorSpaceEntity
      case _ => throw new IllegalArgumentException("Ungültiger SelectorStarlinkSat")
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