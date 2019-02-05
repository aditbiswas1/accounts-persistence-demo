package accounts.domain

trait AggregateRoot[T <: AggregateRoot[T, E], E] {
  self: T =>

  def id: Id[T]

  def applyEvent: PartialFunction[E, T]

}
