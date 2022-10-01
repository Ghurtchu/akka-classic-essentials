package part2actors.ping_pong

import akka.actor.{Actor, ActorSystem, Props}

object PingPongSimulation extends scala.App {

  case object Ping
  case object Pong

  class PingPongActor extends Actor {
    override def receive: Receive = {
      case Ping => println("ping"); Thread sleep 250; self ! Pong
      case Pong => println("pong"); Thread sleep 250; self ! Ping
    }
  }

  val system = ActorSystem("PingPongSystem")

  val actor = system.actorOf(Props[PingPongActor], "PingPongActor")

  actor ! Ping



}
