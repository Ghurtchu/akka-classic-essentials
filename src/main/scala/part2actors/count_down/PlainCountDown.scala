package part2actors.count_down

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object PlainCountDown extends scala.App {

  import CountDownActor._


  class CountDownActor extends Actor {

    override def receive: Receive = {

      case Start(from, actorRef) =>
        println(s"Starting decrementing from $from")
        println("Sending Decrement message to actor")
        actorRef ! Decrement(from)

      case Decrement(current) => self ! (if (current > 0) {
        println(this) // actual actor
        println(s"current is $current")
        Decrement(current - 1)
      } else Terminate)

      case Terminate => println("Finished counting down to 0")

    }

  }

  object CountDownActor {

    final case class Start(from: Int, actorRef: ActorRef)
    final case class Decrement(current: Int)
    case object Terminate

  }

  val system = ActorSystem("CountDownSystem")

  val startActor= system.actorOf(Props[CountDownActor], "startActor")
  val decrementActor = system.actorOf(Props[CountDownActor], "decrementActor")

  startActor ! Start(100, decrementActor)

  system.terminate()
}
