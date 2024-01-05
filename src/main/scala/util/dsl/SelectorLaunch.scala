package SpaceData.util.dsl

sealed trait SelectorLaunch

final case object allLaunches extends SelectorLaunch
final case object succeeded extends SelectorLaunch
final case object failed extends SelectorLaunch