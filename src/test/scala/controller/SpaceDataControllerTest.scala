package controller

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import SpaceData.model.{SpaceEntity, StarlinkSat, Rocket}
import SpaceData.controller.{SpaceDataController, SelectorSpaceEntity, active, inactive, all}
import SpaceData.controller.kafka.SpaceDataConsumer
import SpaceData.controller.spark.SpaceDataSparkConsumer
import org.scalatest.wordspec.AnyWordSpec

class SpaceDataControllerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  // Mocking the dependencies
  val mockedSpaceDataConsumer: SpaceDataConsumer = mock[SpaceDataConsumer]

  // Creating an instance of the SpaceDataController with the mocked dependencies
  val spaceDataController = new SpaceDataController() {
    override val consumerController: SpaceDataConsumer = mockedSpaceDataConsumer
  }

  // StarlinkSat Mocks
  private val starlinkSatMocks
  = List( StarlinkSat("starlink", "Starlink-1", "123", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 1, active = true, None),
          StarlinkSat("starlink", "Starlink-2", "456", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 0, active = false, None),
          StarlinkSat("starlink", "Starlink-3", "789", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 1, active = true, None))
  private val starlinkSatMocksActive
  = List( StarlinkSat("starlink", "Starlink-1", "123", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 1, active = true, None),
          StarlinkSat("starlink", "Starlink-3", "789", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 1, active = true, None))
  private val starlinkSatMocksInactive
  = List( StarlinkSat("starlink", "Starlink-2", "456", "2022-01-01", 100.0, 550.0, -75.0, 132.5, 100, 0, active = false, None))

  // Rocket Mocks
  private val rocketMocks = List( Rocket("rocket", "Rocket-1", "011", true),
                                  Rocket("rocket", "Rocket-2", "012", false),
                                  Rocket("rocket", "Rocket-3", "013", true))
  private val rocketMocksActive = List( Rocket("rocket", "Rocket-1", "011", true),
                                        Rocket("rocket", "Rocket-3", "013", true))
  private val rocketMocksInactive = List( Rocket("rocket", "Rocket-2", "012", false))

  "SpaceDataController" should {
    
    "return false if Lists are NOT empty" in {
      when(mockedSpaceDataConsumer.rocketslistAll).thenReturn(starlinkSatMocks)
      when(mockedSpaceDataConsumer.rocketslisActive).thenReturn(starlinkSatMocksActive)
      when(mockedSpaceDataConsumer.rocketslisInactive).thenReturn(starlinkSatMocksInactive)
      when(mockedSpaceDataConsumer.starlinksatlistAll).thenReturn(rocketMocks)
      when(mockedSpaceDataConsumer.starlinksatlistActive).thenReturn(rocketMocksActive)
      when(mockedSpaceDataConsumer.starlinksatlistInactive).thenReturn(rocketMocksInactive)

      spaceDataController.checkListsNotEmpty() should be(false)
    }

    "return true if Lists are empty" in {
      when(mockedSpaceDataConsumer.rocketslistAll).thenReturn(List())
      when(mockedSpaceDataConsumer.rocketslisActive).thenReturn(List())
      when(mockedSpaceDataConsumer.rocketslisInactive).thenReturn(List())
      when(mockedSpaceDataConsumer.starlinksatlistAll).thenReturn(List())
      when(mockedSpaceDataConsumer.starlinksatlistActive).thenReturn(List())
      when(mockedSpaceDataConsumer.starlinksatlistInactive).thenReturn(List())

      spaceDataController.checkListsNotEmpty() should be(true)
    }

    "return SpaceEntities List for given criterias" in {
      when(mockedSpaceDataConsumer.rocketslistAll).thenReturn(rocketMocks)
      when(mockedSpaceDataConsumer.rocketslisActive).thenReturn(rocketMocksActive)
      when(mockedSpaceDataConsumer.rocketslisInactive).thenReturn(rocketMocksInactive)
      when(mockedSpaceDataConsumer.starlinksatlistAll).thenReturn(starlinkSatMocks)
      when(mockedSpaceDataConsumer.starlinksatlistActive).thenReturn(starlinkSatMocksActive)
      when(mockedSpaceDataConsumer.starlinksatlistInactive).thenReturn(starlinkSatMocksInactive)

      spaceDataController.getSpaceEntitiesList("all", "starlinksat") should be(starlinkSatMocks)
      spaceDataController.getSpaceEntitiesList("active", "starlinksat") should be(starlinkSatMocksActive)
      spaceDataController.getSpaceEntitiesList("inactive", "starlinksat") should be(starlinkSatMocksInactive)
      spaceDataController.getSpaceEntitiesList("all", "rocket") should be(rocketMocks)
      spaceDataController.getSpaceEntitiesList("active", "rocket") should be(rocketMocksActive)
      spaceDataController.getSpaceEntitiesList("inactive", "rocket") should be(rocketMocksInactive)

      // Test for invalid entity type
      val invalidEntity = "invalidEntity"
      val exception = intercept[IllegalArgumentException] {
        spaceDataController.getSpaceEntitiesList("all", invalidEntity)
      }
      exception.getMessage() should be(s"Unsupported entity type: $invalidEntity")
    }

    "return a specific StarlinkSat by given Id" in {
      when(mockedSpaceDataConsumer.starlinksatlistAll).thenReturn(starlinkSatMocks)

      spaceDataController.getSpaceEntitiyDetails("123", "starlinksat") should be(Some(starlinkSatMocks(0)))
      spaceDataController.getSpaceEntitiyDetails("456", "starlinksat") should be(Some(starlinkSatMocks(1)))
      spaceDataController.getSpaceEntitiyDetails("789", "starlinksat") should be(Some(starlinkSatMocks(2)))
      spaceDataController.getSpaceEntitiyDetails("000", "rocket") should be(None)
    }

    "match the SelectorAsString to the corresponding SelectorSpaceEntity" in {
      
      spaceDataController.stringToSelecorSpaceEntity("active") should be(active)
      spaceDataController.stringToSelecorSpaceEntity("inactive") should be(inactive)
      //spaceDataController.stringToSelecorSpaceEntity("all") should be(all)

      // Test for invalid entity type
      val invalidSelector = "invalidSelector"
      val exception = intercept[IllegalArgumentException] {
        spaceDataController.stringToSelecorSpaceEntity(invalidSelector)
      }
      exception.getMessage() should be("Ungültiger SelectorSpaceEntity")
    }

    "return the corresponding Dashboard values" in {
      when(mockedSpaceDataConsumer.rocketslistAll).thenReturn(rocketMocks)
      when(mockedSpaceDataConsumer.rocketslisActive).thenReturn(rocketMocksActive)
      when(mockedSpaceDataConsumer.rocketslisInactive).thenReturn(rocketMocksInactive)
      when(mockedSpaceDataConsumer.starlinksatlistAll).thenReturn(starlinkSatMocks)
      when(mockedSpaceDataConsumer.starlinksatlistActive).thenReturn(starlinkSatMocksActive)
      when(mockedSpaceDataConsumer.starlinksatlistInactive).thenReturn(starlinkSatMocksInactive)

      val starlinkSatValues = List(("all", 3), ("active", 2), ("inactive", 1))
      val rocketValues = List(("all", 3), ("active", 2), ("inactive", 1))
      spaceDataController.getDashboardValues() should be(starlinkSatValues, rocketValues)
    }
  }
  

  // Add other tests with mock behaviors...
}
