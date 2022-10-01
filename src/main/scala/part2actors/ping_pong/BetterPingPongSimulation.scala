package part2actors.ping_pong

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object BetterPingPongSimulation extends scala.App {

  final case class Pong(actorRef: ActorRef)
  final case class Ping(actorRef: ActorRef)

  class Pinger extends Actor {
    override def receive: Receive = {
      case Pong(ref) =>
        println("Ping!")
        Thread sleep 250
        ref ! Ping(self)
    }
  }

  class Ponger extends Actor {
    override def receive: Receive = {
      case Ping(ref) =>
        println("Pong!")
        Thread sleep 250
        ref ! Pong(self)
    }
  }

  val system = ActorSystem("PingPongSystem")

  val pinger = system.actorOf(Props[Pinger], "pinger")
  val ponger = system.actorOf(Props[Ponger], "ponger")

  pinger ! Pong(ponger)

}
