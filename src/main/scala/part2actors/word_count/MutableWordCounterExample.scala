package part2actors.word_count

import akka.actor.{Actor, ActorSystem, Props}

object MutableWordCounterExample extends scala.App {

  class WordCounter extends Actor {

    import WordCounter._

    private final var count: Int = 0 // initialize to 0

    override def receive: Receive = {
      case Sentence(c) => count += c.split(" ").length
      case Print       => println(s"current count = $count")
    }

  }

  object WordCounter {
    final case class Sentence(content: String)
    case object Print
  }

  val system = ActorSystem("WordCountingSystem")

  val actor = system.actorOf(Props[WordCounter], "wordCounter")

  import WordCounter._

  for (i <- 1 to 5) {
    actor ! Sentence("This is a message")
    Thread sleep 250
  }

  actor ! Print

}
