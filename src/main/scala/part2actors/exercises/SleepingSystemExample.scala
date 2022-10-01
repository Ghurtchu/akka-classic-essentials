package part2actors.exercises

import akka.actor.{Actor, ActorSystem, Props}
import part2actors.exercises.SleepingSystemExample.AchiJaparidze.{Decrement, Increment, Sleep}

object SleepingSystemExample extends scala.App {

  object AchiJaparidze {
    case object Increment
    case object Decrement
    case object Sleep
  }

  class AchiJaparidze extends Actor {

    import AchiJaparidze._

    override def receive: Receive = onChange(0)

    def onChange(current: Int): Receive = {
      case Increment => println("incrementing"); context.become(onChange(current + 1))
      case Decrement => println("decrementing"); context.become(onChange(current - 1))
      case Sleep     => println(s"count = $current")
    }

  }

  val system = ActorSystem("AchiJaparidzeSleepingSytem")

  val actor = system.actorOf(Props[AchiJaparidze], "AchiJaparidze")

  (0 to 3).foreach(_ => actor ! Increment)
  (0 to 3).foreach(_ => actor ! Decrement)

  actor ! Sleep

}
