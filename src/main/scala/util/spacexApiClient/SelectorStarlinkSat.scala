package SpaceData.util.spacexApiClient

sealed trait SelectorStarlinkSat

final case object all extends SelectorStarlinkSat
final case object active extends SelectorStarlinkSat
final case object inactive extends SelectorStarlinkSat