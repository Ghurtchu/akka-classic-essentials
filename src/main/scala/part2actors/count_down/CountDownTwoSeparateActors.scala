package part2actors.count_down

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object CountDownTwoSeparateActors extends scala.App {

  class StartCountdownActor extends Actor {
    override def receive: Receive = {
      case Start(from, actorRef) =>
        println(self.path.name)
        println(s"sending decrement msg to ${actorRef.path.name}")
        actorRef ! Decrement(from)
    }
  }

  class DecrementActor extends Actor {
    override def receive: Receive = {
      case Decrement(current) => self ! (if (current > 0) {
        println(s"current $current")
        println(self.path.name)
        Decrement(current - 1)
      } else Terminate)
      case Terminate          => println("Finished"); context.system.terminate()
    }
  }

  final case class Start(from: Int, actorRef: ActorRef)
  final case class Decrement(current: Int)
  case object Terminate

  val system = ActorSystem("CountDownSystem")

  val startActor     = system.actorOf(Props[StartCountdownActor], "StartActor")
  val decrementActor = system.actorOf(Props[DecrementActor], "DecrementActor")

  startActor ! Start(10000, decrementActor)

  println("End")

}
