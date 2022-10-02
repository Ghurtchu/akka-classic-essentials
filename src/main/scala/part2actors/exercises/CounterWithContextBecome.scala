package part2actors.exercises

import akka.actor.{Actor, ActorSystem, Props}

object CounterWithContextBecome extends scala.App {


  case object Increment
  case object Decrement
  case object Terminate

  class CounterActor extends Actor {
    override def receive: Receive = onChange(0)

    private def onChange(count: Int): Receive = {
      case Increment => context.become(onChange(count + 1))
      case Decrement => context.become(onChange(count - 1))
      case Terminate => println(s"count = $count"); context.system.terminate()
    }
  }

  val system = ActorSystem("sys")

  val actor = system.actorOf(Props[CounterActor])

  actor ! Increment
  actor ! Decrement
  actor ! Increment
  actor ! Increment
  actor ! Decrement

  actor ! Terminate

}
