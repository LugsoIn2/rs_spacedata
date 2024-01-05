package SpaceData.model

case class Launch( 
    name: String,
    id: String,
    date_utc: String,
    launchpad: String,
    success: Boolean
    ) { }