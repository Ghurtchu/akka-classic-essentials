package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.ChangingActorBehaviours.FussyKid.HAPPY
import part2actors.ChangingActorBehaviours.Mom.{Ask, CHOCOLATE, Food, MomStart, VEGETABLE}

object ChangingActorBehaviours extends scala.App {

  class FussyKid extends Actor {
    import FussyKid._
    import Mom._

    // internal state of the kid in the beginning
    var state = HAPPY

    override def receive: Receive = {
      case Food(VEGETABLE) => {
        println("WHAT? VEGGIES AGAIN! UUUUUUGHHHHHHHHH")
        state = SAD
      }
      case Food(CHOCOLATE) => {
        println("YAYYYYYYYYY! CHOCOLATE!")
        state = HAPPY
      }
      case Ask(_)          => {
        val response = if (state == HAPPY) "let's play!" else "I hate you"
        Thread sleep 500
        println(response)
        sender() ! (if (state == HAPPY) Accept else Reject)
      }
    }
  }

  // messages to be responded from the instance of the Child actor
  object FussyKid {

    case object Accept
    case object Reject

    val HAPPY = "happy"
    val SAD   = "sad"

  }

  class Mom extends Actor {

    import FussyKid._

    override def receive: Receive = {
      case MomStart(ref) =>  {
        // test our interaction here
        val food = if (scala.util.Random.nextBoolean()) VEGETABLE else CHOCOLATE
        println(s"I am giving my child $food")
        Thread sleep 500
        ref ! Food(food)
        Thread sleep 500
        val question = "do you want to play?"
        println(question)
        ref ! Ask(question)
      }
      case Accept =>
        Thread sleep 500
        println("Yay! my kid is happy! :)")
      case Reject =>
        Thread sleep 500
        println("My kid is sad :( but at least he's healthy! :)")
    }

  }

  // messages to be sent from the instance of Mom actor
  object Mom {

    final case class MomStart(ref: ActorRef) // from the main actor or dead/default actor for starting the app
    final case class Food(food: String)
    final case class Ask(msg: String) // do you want to play?

    val VEGETABLE = "veggies"
    val CHOCOLATE = "chocolate"

  }

  class StatelessFussyKid extends Actor {

    import FussyKid._
    import Mom._

    override def receive: Receive = happyReceive // let's choose it by default

    def happyReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive, false) // change my handler to sadReceive
      case Food(CHOCOLATE) => // stay happy
      case Ask(_)          => sender() ! Accept
    }

    def sadReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive, false)
      case Food(CHOCOLATE) => context.unbecome() // change my handler to happyReceive
      case Ask(_)          => sender() ! Reject
    }

  }

  val system = ActorSystem("changingActorBehaviourDemo")

  val fussyKid     = system.actorOf(Props[FussyKid], "FussyKidActor")
  val mom          = system.actorOf(Props[Mom], "MomActor")
  val statelessKid = system.actorOf(Props[StatelessFussyKid], "StatelessKid")

  while (true) {
    mom ! MomStart(statelessKid)
    Thread sleep 5000
  }

}
