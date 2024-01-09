package SpaceData.controller

sealed trait SelectorSpaceEntity

final case object all extends SelectorSpaceEntity
final case object active extends SelectorSpaceEntity
final case object inactive extends SelectorSpaceEntity