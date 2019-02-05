package accounts

import accounts.actors.AccountActor
import accounts.domain.{AccountId, Deposit, StartAccount, Withdraw}
import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.pattern.ask
import akka.util.Timeout
import scala.io.StdIn
import scala.concurrent.duration._

object AccountsApp {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("account-system")

    try {
      println("hello world")
      val account_id = AccountId.createRandom

      val account_actor: ActorRef = system.actorOf(AccountActor.props(account_id))

      implicit val timeout = Timeout(5 seconds)

      account_actor ! "printId"
      account_actor ? StartAccount(100)
      account_actor ! "print"
      account_actor ? Deposit(10)
      account_actor ! "print"
      account_actor ? Withdraw(100)
      account_actor ! "print"
      StdIn.readLine()
    } finally {
      system.terminate()
    }
  }
}
