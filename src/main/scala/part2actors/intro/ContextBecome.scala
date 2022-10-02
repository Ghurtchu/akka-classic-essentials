package part2actors.intro

import akka.actor.{Actor, ActorSystem, Props}

object ContextBecome extends scala.App {

  class SimpleActor extends Actor {
    override def receive: Receive = onChange(0)

    def onChange(n: Int): Receive = {
      case 0     => println("received 0, returning 100"); sender ! 100
      case other => println(s"received $other turning it into 0"); context.become(onChange(0))
    }
  }

  val system = ActorSystem("System")

  val actor = system.actorOf(Props[SimpleActor])

  actor ! 0
  actor ! 1
  actor ! 34
  actor ! 123

}
