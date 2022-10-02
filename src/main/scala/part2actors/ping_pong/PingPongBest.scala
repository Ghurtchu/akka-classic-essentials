package part2actors.ping_pong

import akka.actor.{Actor, ActorSystem, Props}

object PingPongBest extends scala.App {

  case object Ping
  case object Pong

  class PingPongActor extends Actor {
    override def receive: Receive = {
      case Ping => println("pong"); sender ! Pong
      case Pong => println("ping"); sender ! Ping
    }
  }

  val system = ActorSystem("PingPongSystem")

  val pinger = system.actorOf(Props[PingPongActor], "Pinger")
  val ponger = system.actorOf(Props[PingPongActor], "Ponger")

  pinger.tell(Ping, ponger)
}
