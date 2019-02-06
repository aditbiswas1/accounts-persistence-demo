package accounts.domain

object Account {
  def create(id: AccountId) = UninitializedAccount(id, 0, 0)
}

sealed trait Account extends AggregateRoot[Account, AccountEvent] {
  def id: AccountId
  def limit: Int
  def balance: Int

  def handleCommand(command: AccountCommand): AccountEvent = command match {
    case StartAccount(balance) => this match {
      case acc: UninitializedAccount => AccountStarted(id, 0, balance)
    }

    case Withdraw(value) => this match {
      case acc: OpenAccount => BalanceChanged(id, value)
    }

    case Deposit(value) => this match {
      case acc: OpenAccount => BalanceChanged(id, -1 * value)
    }
  }
}

final case class UninitializedAccount(override val id: AccountId, override val limit: Int, override val balance: Int) extends Account {

  override def applyEvent: PartialFunction[AccountEvent, OpenAccount] = {
    case ev@AccountStarted(_, limit, balance) => OpenAccount(id, limit, balance)
  }
}

final case class OpenAccount(override val id: AccountId, override val limit: Int, override val balance: Int) extends Account {

  override def applyEvent: PartialFunction[AccountEvent, Account] = {
    case ev@BalanceChanged(_, delta) =>
      if (balance - delta <= limit) {
        ClosedAccount(id, limit, balance - delta)
      }
      else {
        OpenAccount(id, limit, balance - delta)
      }
  }
}

final case class ClosedAccount(override val id: AccountId, override val limit: Int, override val balance: Int) extends Account {
  override def applyEvent: PartialFunction[AccountEvent, Account] = PartialFunction.empty
}