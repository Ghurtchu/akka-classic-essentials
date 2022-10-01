package part2actors.word_count

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ImmutableWordCounterExample extends scala.App {

  import WordCounter._

  class WordCounter extends Actor {

    override def receive: Receive = onSentence(0)

    def onSentence(count: Int): Receive = {
      case Sentence(content) => context.become(onSentence(count + content.split(" ").length))
      case Print             => println(s"count $count")
    }

  }

  object WordCounter {
    final case class Sentence(content: String)
    case object Print
  }

  val system = ActorSystem("ImmutableWordCountingSystem")

  val actor = system.actorOf(Props[WordCounter], "immutableWordCounter")

  for (_ <- 0 to 10) actor ! Sentence("This is a message")

  actor ! Print

}
