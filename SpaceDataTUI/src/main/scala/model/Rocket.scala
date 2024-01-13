package SpaceDataTUI.model
import SpaceDataTUI.model.SpaceEntity


case class Rocket( 
    name: String,
    id: String,
    active: Boolean) extends SpaceEntity {
        override def toString: String = s"ID: ${id}\nName: ${name}\n" +
        s"Active: ${active}"
    }