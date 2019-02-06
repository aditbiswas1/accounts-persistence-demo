package accounts

import accounts.actors.AccountActor
import accounts.actors.AccountActor.AccountResponse
import accounts.domain._
import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.Done
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol._

import scala.io.StdIn
import scala.concurrent.duration._
import scala.concurrent.Future

object AccountsApp {
  implicit val system = ActorSystem("account-system")
  implicit val materializer = ActorMaterializer()

  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(5 seconds)

  // formats for unmarshalling and marshalling
  implicit val accountFormat = jsonFormat3(AccountResponse)
  implicit val depositFormat = jsonFormat1(Withdraw)
  implicit val withdrawFormat = jsonFormat1(Deposit)


  def main(args: Array[String]): Unit = {
    val account_id = AccountId.createRandom

    val account_actor: ActorRef = system.actorOf(AccountActor.props(account_id))
    account_actor ? StartAccount(100)
    val route: Route =
      pathPrefix("account") {
        post {
          complete("Hello")
        } ~
          get {
            {
              // there might be no item for a given id
              val maybeAccount = account_actor ? "get_account"

              onSuccess(maybeAccount) {
                case account: AccountResponse => complete(account)
              }
            }
          } ~
          path("withdraw") {
            post {

              entity(as[Withdraw]) { withdraw_command =>
                val saved = account_actor ? withdraw_command
                onComplete(saved) { CommandAccepted =>
                  complete("withdraw accepted")
                }
              }

            }
          } ~
          path("deposit") {
            post {
              entity(as[Deposit]) { deposit_command =>
                val saved = account_actor ? deposit_command
                onComplete(saved) { CommandAccepted =>
                  complete("deposit accepted")
                }
              }
            }

          }
      }


    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ ⇒ system.terminate()) // and shutdown when done

  }
}
