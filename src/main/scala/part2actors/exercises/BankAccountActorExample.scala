package part2actors.exercises

import akka.actor.{Actor, ActorRef, ActorSystem, Props, actorRef2Scala}

object BankAccountActorExample extends scala.App {

  import BankAccount._

  object BankAccount {
    final case class Deposit(amount: Int)
    final case class Withdraw(amount: Int)
    final case class TransactionSuccess(msg: String)
    final case class TransactionFailure(msg: String)
    final case class Interact(msg: Any, actorRef: ActorRef)
    case object Statement


    def props(amount: Int): Props = Props(new BankAccount(amount))
  }

  class BankAccount(curr: Int) extends Actor {

    override def receive: Receive = onChange(curr)

    def onChange(current: Int): Receive = {
      case Deposit(amount)  =>
        if (amount <= 0) sender ! TransactionFailure("Amount should be positive")
        else {
          context.become(onChange(current + amount))
          println(s"curr = $current")
          println(s"Depositing $amount")
          sender ! TransactionSuccess(s"Deposited $amount successfully")
        }

      case Withdraw(amount) =>
        if (amount > current) sender ! TransactionFailure("Amount is more than current")
        else {
          context.become(onChange(current - amount))
          println(s"curr = $current")
          println(s"Withdrawing $amount")
          sender ! TransactionSuccess(s"Withdrawn $amount successfully")
        }

      case Statement        =>
    }

  }

  class Person extends Actor {
    override def receive: Receive = {
      case Interact(msg, ref) => ref ! msg
      case msg: Any           => println(msg)
    }
  }

  val system = ActorSystem("BankAccountOperationsSystem")

  val person = system.actorOf(Props[Person], "person")
  val acc    = system.actorOf(BankAccount.props(1000), "acc")

  person ! Interact(Deposit(100_000), acc)
  person ! Interact(Withdraw(100_000), acc)

}
