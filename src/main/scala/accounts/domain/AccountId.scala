package accounts.domain

import java.util.UUID

object AccountId {
  def createRandom = AccountId(UUID.randomUUID().toString())
}

case class AccountId(override val value: String) extends AnyVal with Id[Account]