package accounts.domain

sealed trait AccountCommand

case class StartAccount(limit: Int) extends AccountCommand

case class Deposit(value: Int) extends AccountCommand

case class Withdraw(value: Int) extends AccountCommand
