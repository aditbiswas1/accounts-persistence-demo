package accounts.domain

sealed trait AccountEvent {
  def id: AccountId
}

case class AccountStarted(
                           override val id: AccountId,
                           limit: Int,
                           balance: Int,
                         ) extends AccountEvent

case class BalanceChanged(
                           override val id: AccountId,
                           delta: Int
                         ) extends AccountEvent

case class AccountClosed(
                          override val id: AccountId
                        ) extends AccountEvent
