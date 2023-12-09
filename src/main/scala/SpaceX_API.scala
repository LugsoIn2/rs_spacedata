import model.StarlinkSat

object SpaceX_API {

    var starlinkSats: List[StarlinkSat] = List().empty
    //var launches: List[Launch] = List().empty

    def getStarlinkSats(slct: Selector): List[StarlinkSat] = {
        case all => starlinkSats
        case active => starlinkSats.filter(_.active)
        case inactive => starlinkSats.filter(!_.active)
    }

    /*def getAllLaunches(): List[Launch] = {
        launches
    }*/
}