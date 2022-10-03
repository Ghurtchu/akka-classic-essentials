package part2actors.cfg

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object IntroAkkaConfig extends scala.App {

  class SimpleLoggingActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case msg => log.info(msg.toString)
    }
  }

  /**
   * 1 - inline configuration
   */

  val configStr =
    """
      |akka {
      |  loglevel = ERROR
      |}
      |""".stripMargin
  // DEBUG, INFO, WARN, ERROR

  val config = ConfigFactory.parseString(configStr)

  val system = ActorSystem("ConfigDemo", config)

  val actor = system.actorOf(Props[SimpleLoggingActor])

  actor ! "A message to remember"

}
