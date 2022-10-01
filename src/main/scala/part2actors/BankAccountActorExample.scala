package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.BankAccountActorExample.Person.LiveTheLife

// reqs:
// Bank account as an actor
// receives:
// - deposit an amount
// - withdraw an amount
// - Statement
// replies with
// - Success
// - Failure
// interact with some other kind of actor

object BankAccountActorExample extends scala.App {

  class BankAccount extends Actor {

    import BankAccount._

    private var amount: Int = 0

    override def receive: Receive = {

      case Deposit(amount)  =>
        if (amount > 0) {
          this.amount += amount
          sender() ! TransactionSuccess(s"Successfully deposited $amount")
        } else
          sender() ! TransactionFailure("Invalid deposit amount")

      case Withdraw(amount) =>
        if (amount < 0) {
          sender() ! TransactionFailure("Invalid withdraw amount")
        } else if (amount > this.amount) {
          sender() ! TransactionFailure("Withdraw amount is more than existing amount")
        } else {
          this.amount -= amount
          sender() ! TransactionSuccess(s"Successfully Withdrew amount: $amount")
        }

      case Statement => sender() ! s"Your current balance: $amount"

     }
  }

  object BankAccount {

    final case class Deposit(amount: Int)
    final case class Withdraw(amount: Int)

    final case class TransactionSuccess(msg: String)
    final case class TransactionFailure(rsn: String)

    case object Statement

  }

  class Person extends Actor {

    import Person._
    import BankAccount._

    override def receive: Receive = {
      case LiveTheLife(acc) =>
        acc ! Deposit(10_000)
        acc ! Withdraw(90_000)
        acc ! Withdraw(500)
        acc ! Statement
      case msg: Any         => println(msg.toString)
    }

  }

  object Person {
    case class LiveTheLife(account: ActorRef)
  }

  val system = ActorSystem("BankSystem")

  val account = system.actorOf(Props[BankAccount], "bankAccount")
  val person  = system.actorOf(Props[Person], "billionaire")

  person ! LiveTheLife(account)

}
