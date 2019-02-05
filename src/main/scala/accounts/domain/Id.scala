package accounts.domain

trait Id[T] extends Any {
  def value: String
}