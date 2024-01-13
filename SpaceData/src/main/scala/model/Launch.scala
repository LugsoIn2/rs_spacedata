package SpaceData.model

case class Launch( 
    name: String,
    id: String,
    date_utc: String,
    launchpad: String,
    success: Boolean
    ) { 
        override def toString: String = s"ID: ${id}\nName: ${name}\n" +
          s"Launch Date: ${date_utc}\nLaunchpad: ${launchpad}\n" +
          s"Success: ${success}\n"
    }