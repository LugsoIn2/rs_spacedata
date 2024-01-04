sealed trait Selector

final case object all extends Selector
final case object active extends Selector
final case object inactive extends Selector