// SpaceDataController.scala
package SpaceData.controller
import SpaceData.model.{StarlinkSat, Launch, Rocket, SpaceEntity}
import SpaceData.controller.SpaceDataStarLinkController
import SpaceData.util.spacexApiClient._
import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import akka.pattern.ask
import scala.concurrent.Future
import javax.xml.crypto.Data
import akka.util.Timeout


class SpaceDataController() {
  implicit val httpActorSystem: ActorSystem = ActorSystem("HttpActorSystem")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  
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
    val selector = stringToSelecorStarlinkSat(slct)
    selector match {
        case `all` => {
          //starlinksatlist
          implicit val timeout: Timeout = Timeout(10.seconds)
          val futureStarlinkSats: Future[Any] = httpClientActorStarlinkSats ? GetCurrentState
          Await.result(futureStarlinkSats, timeout.duration).asInstanceOf[List[SpaceEntity]]
        } case `active` => {
          //starlinksatlistActive
          //httpClientActorStarlinkSats ! GetCurrentState
          List.empty
      } case `inactive` => {
          //starlinksatlistInactive
          //httpClientActorStarlinkSats ! GetCurrentState
          List.empty
      }
    }
  }

  // def getStarlinkSatDetails(id: String): Option[StarlinkSat] = {
  //   val foundStarlinkSat: Option[StarlinkSat] = findStarlinkSatById(starlinksatlist,id)
  //   foundStarlinkSat match {
  //     case Some(starlinkSat) =>
  //       Some(starlinkSat)
  //     case None =>
  //       None
  //   }
  // }

  def findStarlinkSatById(starlinkSats: List[StarlinkSat], targetId: String): Option[StarlinkSat] = {
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

  // def getDashboardValues(): (List[(String, Int)],List[(String, Int)]) = {
  //   var dashbStarlinkVals: List[(String, Int)] = List.empty[(String, Int)]
  //   var dashbLaunchVals: List[(String, Int)] = List.empty[(String, Int)]
  //   dashbStarlinkVals = dashbStarlinkVals :+ ("all", starlinksatlist.size)
  //   dashbStarlinkVals = dashbStarlinkVals :+ ("active", starlinksatlistActive.size)
  //   dashbStarlinkVals = dashbStarlinkVals :+ ("inactive", starlinksatlistInactive.size)
  //   dashbLaunchVals = dashbLaunchVals :+ ("allLaunches", launcheslist.size)
  //   dashbLaunchVals = dashbLaunchVals :+ ("succeeded", launcheslist.size)
  //   dashbLaunchVals = dashbLaunchVals :+ ("failed", launcheslist.size)
  //   (dashbStarlinkVals, dashbLaunchVals)
  // }


  def stringToSelecorStarlinkSat(slct: String): SelectorStarlinkSat = {
      //val selector: Selector
      slct.toLowerCase match {
      case "all" => all: SelectorStarlinkSat
      case "active" => active: SelectorStarlinkSat
      case "inactive" => inactive: SelectorStarlinkSat
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