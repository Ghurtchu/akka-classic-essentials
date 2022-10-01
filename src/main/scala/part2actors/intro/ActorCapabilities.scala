package part2actors.intro

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorCapabilities extends scala.App {

  class SimpleActor extends Actor {

    override def receive: Receive = {
      case "Hi!" => context.sender() ! "Hello, there" // replying to a message
      case message: String => println(s"[simple actor] ${context.self} I have received `$message`")
      case num: Int => println(s"[simple actor] I have received a number `$num`")
      case specialMessage: SpecialMessage => println(s"[simple actor] I have received a special msg `${specialMessage.contents}`")
      case selfMessage: SelfMessage => self ! selfMessage.content
      case SayHiTo(actorRef) => actorRef ! "Hi!"
      case WirelessPhoneMessage(ct, ref) => ref forward ct.concat("s") // alice received but forwarded to bob with Hi! + s which is "Hi!s"
    }
  }

  val system = ActorSystem("actorCapabilitiesDemo")
  val simpleActor = system.actorOf(Props[SimpleActor], "simpleActor")

  simpleActor ! "hello, actor"

  // 1 - messages can be of any type conforming two cases:
  // a) message must be IMMUTABLE (basically final fields in Java or val fields in Scala)
  // b) messages must be SERIALIZABLE (java interface)
  // in practice use case classes and case objects

  // 2 - actors have information about their context and about themselves
  // each actor has a member variable called context: ActorContext
  // context.self is the Akka ActorRef equivalent of "this" in OOP world.

  final case class SelfMessage(content: String)

  simpleActor ! SelfMessage("I am an Actor and I am proud of it")

  simpleActor ! 42

  final case class SpecialMessage(contents: String)

  simpleActor ! SpecialMessage("A very very special message")

  // everything happens in a non-blocking and async way

  // 3 - actors can REPLY to messages
  val alice = system.actorOf(Props[SimpleActor], "alice")
  val bob = system.actorOf(Props[SimpleActor], "bob")

  case class SayHiTo(actorRef: ActorRef)

  alice ! SayHiTo(bob)

  alice ! "Hi" // reply to "me" but I'm null

  // 5 - forwarding messages
  // D -> A -> B
  // forwarding = sending a message with the ORIGINAL sender

  case class WirelessPhoneMessage(content: String, ref: ActorRef)

  alice ! WirelessPhoneMessage("Hi!", bob)


}
