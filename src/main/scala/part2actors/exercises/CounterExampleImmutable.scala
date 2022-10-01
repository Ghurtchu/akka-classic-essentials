package part2actors.exercises

import akka.actor.{Actor, ActorSystem, Props}

object CounterExampleImmutable extends scala.App {

  import CounterActor._

  object CounterActor {
    case object Increment
    case object Decrement
    case object Display
  }

  class CounterActor extends Actor {

    override def receive: Receive = onChange(0)

    def onChange(n: Int): Receive = {
      case Increment => context.become(onChange(n + 1))
      case Decrement => context.become(onChange(n - 1))
      case Display   => println(n)
    }

  }

  val system = ActorSystem("System")

  val actor = system.actorOf(Props[CounterActor], "actor")

  actor ! Increment
  actor ! Increment
  actor ! Decrement
  actor ! Display

}
