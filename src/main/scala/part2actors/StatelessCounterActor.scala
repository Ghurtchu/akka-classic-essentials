package part2actors

import akka.actor.{Actor, ActorSystem, Props}
import part2actors.StatelessCounterActor.CounterActor.Print

object StatelessCounterActor extends scala.App {

  import CounterActor._

  // 1 - recreate the Counter actor with context.become() and NO MUTABLE STATE
  // 2 - simplified voting system

  object CounterActor {
    case object Increment
    case object Decrement
    case object Print
  }

  class CounterActor extends Actor {

    override def receive: Receive = countReceived(0)

    def countReceived(current: Int): Receive = {
      case Increment =>
        println(s"incrementing $current ===> ${current + 1}")
        context.become(countReceived(current + 1))
      case Decrement =>
        println(s"decrementing $current ===> ${current - 1}")
        context.become(countReceived(current - 1))
      case Print     => println(s"[counter] my current count is $current")
    }

  }

  val system = ActorSystem("system")

  val actor = system.actorOf(Props[CounterActor], "actor")

  while (true) {
    actor ! Decrement
    Thread sleep 500
    actor ! Increment
    Thread sleep 500
    actor ! Decrement
    Thread sleep 500
  }

}
