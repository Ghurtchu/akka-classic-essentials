package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object TrafficLight extends scala.App {

  class TrafficActor extends Actor {

    import TrafficActor._

    override def receive: Receive = {

      case Command(light) =>
        println(s"turning on $light")
        Thread sleep 1000
        self ! light.next.asCommand

    }

  }

  object TrafficActor {

    sealed trait Light {
      def next: Light
    }

    object Light {

      case object Red extends Light {
        override def next: Light = Green
      }

      case object Green extends Light {
        override def next: Light = Yellow
      }

      case object Yellow extends Light {
        override def next: Light = Red
      }

      implicit class LightOps(light: Light) {
        def asCommand: Command = Command(light)
      }

    }

    case class Command(light: Light)

  }

  val system = ActorSystem("trafficLightSystem")

  val actor  = system.actorOf(Props[TrafficActor], "trafficActor")

  actor ! TrafficActor.Command(TrafficActor.Light.Red)

}
