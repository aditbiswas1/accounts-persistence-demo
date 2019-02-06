package accounts.actors

import accounts.domain._
import akka.actor.{Actor, ActorLogging, Props}
import akka.persistence.{PersistentActor, SnapshotOffer}

object AccountActor {
  def props(id: AccountId): Props = Props(new AccountActor(id))

  sealed trait CommandResult

  case object CommandAccepted extends CommandResult
  case class AccountResponse(id: String, limit: Int, balance: Int)
}

class AccountActor(id: AccountId) extends PersistentActor with ActorLogging {

  import AccountActor._

  override def persistenceId: String = s"account-${id.value}"

  val snapShotInterval = 1000

  var account: Account = Account.create(id)

  def updateState(event: AccountEvent): Unit = account = account.applyEvent(event)

  def handleResult(event: AccountEvent): Unit = {

    persist(event) { event =>
      updateState(event)
      if (lastSequenceNr % snapShotInterval == 0 && lastSequenceNr != 0)
        saveSnapshot(account)
      sender() ! CommandAccepted
    }
  }

  override def receiveCommand: Receive = {
    case command: AccountCommand =>
      handleResult(account.handleCommand(command))
    case "print" => println(account)
    case "get_account" => sender() ! AccountResponse(account.id.value, account.limit, account.balance)

  }


  override def receiveRecover: Receive = {
    case evt: AccountEvent => updateState(evt)
    case SnapshotOffer(_, snapshot: Account) => account = snapshot
  }

}