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

  def getStarlinkSatList(slct: String): List[SpaceEntity] = {
    val selector = stringToSelecorSpaceEntity(slct)
    selector match {
        case `all` => {
          //starlinksatlist
          implicit val timeout: Timeout = Timeout(10.seconds)
          val futureStarlinkSats: Future[Any] = httpClientActorStarlinkSats ? GetCurrentState
          Await.result(futureStarlinkSats, timeout.duration).asInstanceOf[List[SpaceEntity]]
        } case `active` => {
          val activeStarlinkSats: List[SpaceEntity] = Await.result(getAndfilterStarlinkSats(true, httpClientActorStarlinkSats), 10.seconds)
          activeStarlinkSats
      } case `inactive` => {
          val inactiveStarlinkSats: List[SpaceEntity] = Await.result(getAndfilterStarlinkSats(false, httpClientActorStarlinkSats), 10.seconds)
          inactiveStarlinkSats
      }
    }
  }

  def getRocketList(slct: String): List[SpaceEntity] = {
    val selector = stringToSelecorSpaceEntity(slct)
    selector match {
      case `all` => {
        implicit val timeout: Timeout = Timeout(10.seconds)
        val futureRockets: Future[Any] = httpClientActorRockets ? GetCurrentState
        Await.result(futureRockets, timeout.duration).asInstanceOf[List[SpaceEntity]]
      } case `active` => {
        val activeRockets: List[SpaceEntity] = Await.result(getAndfilterRockets(true, httpClientActorRockets), 10.seconds)
        activeRockets
      } case `inactive` => {
        val inactiveRockets: List[SpaceEntity] = Await.result(getAndfilterRockets(false, httpClientActorRockets), 10.seconds)
        inactiveRockets
      }
    }
  }

  def getAndfilterStarlinkSats(isActive: Boolean, httpClientActor: ActorRef): Future[List[SpaceEntity]] = {
    implicit val timeout: Timeout = Timeout(10.seconds)
    Source.single(())
    .mapAsync(1)(_ => httpClientActor ? GetCurrentState)
    .map {  data => 
      val instances = data.asInstanceOf[List[SpaceEntity]]
      if (isActive) instances.filter(_.asInstanceOf[StarlinkSat].active)
      else instances.filterNot(_.asInstanceOf[StarlinkSat].active)
    }
    .runWith(Sink.seq)
    .map(_.flatten.toList)
  }

  def getAndfilterRockets(isActive: Boolean, httpClientActor: ActorRef): Future[List[SpaceEntity]] = {
    implicit val timeout: Timeout = Timeout(10.seconds)
    Source.single(())
    .mapAsync(1)(_ => httpClientActor ? GetCurrentState)
    .map {  data => 
      val instances = data.asInstanceOf[List[SpaceEntity]]
      if (isActive) instances.filter(_.asInstanceOf[Rocket].active)
      else instances.filterNot(_.asInstanceOf[Rocket].active)
    }
    .runWith(Sink.seq)
    .map(_.flatten.toList)
  }
  
  def getStarlinkSatDetails(id: String): Option[SpaceEntity] = {
    val starlinksatlist = getStarlinkSatList("all")
    val foundStarlinkSat: Option[SpaceEntity] = findStarlinkSatById(starlinksatlist,id)
    foundStarlinkSat match {
      case Some(starlinkSat) =>
        Some(starlinkSat)
      case None =>
        None
    }
  }

  def findStarlinkSatById(starlinkSats: List[SpaceEntity], targetId: String): Option[SpaceEntity] = {
    starlinkSats.find(_.id == targetId)
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

  def getDashboardValues(): (List[(String, Int)],List[(String, Int)]) = {
    var dashbStarlinkVals: List[(String, Int)] = List.empty[(String, Int)]
    var dashbLaunchVals: List[(String, Int)] = List.empty[(String, Int)]
    dashbStarlinkVals = dashbStarlinkVals :+ ("all", getStarlinkSatList("all").size)
    dashbStarlinkVals = dashbStarlinkVals :+ ("active", getStarlinkSatList("active").size)
    dashbStarlinkVals = dashbStarlinkVals :+ ("inactive", getStarlinkSatList("inactive").size)
    dashbLaunchVals = dashbLaunchVals :+ ("allLaunches", launcheslist.size)
    dashbLaunchVals = dashbLaunchVals :+ ("succeeded", launcheslist.size)
    dashbLaunchVals = dashbLaunchVals :+ ("failed", launcheslist.size)
    (dashbStarlinkVals, dashbLaunchVals)
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