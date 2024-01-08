package SpaceData.model
import SpaceData.model.SpaceEntity


case class Rocket( 
    name: String,
    id: String,
    active: Boolean) extends SpaceEntity