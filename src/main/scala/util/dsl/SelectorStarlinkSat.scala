package SpaceData.util.dsl

sealed trait SelectorStarlinkSat

final case object all extends SelectorStarlinkSat
final case object active extends SelectorStarlinkSat
final case object inactive extends SelectorStarlinkSat