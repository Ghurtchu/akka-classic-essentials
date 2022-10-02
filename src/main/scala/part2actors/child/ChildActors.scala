package part2actors.child

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActors extends scala.App {

  // actors can create other actors

  class Parent extends Actor {

    import Parent._

    // will only receive CreateChild message for the first time
    override def receive: Receive = {
      case CreateChild(name) =>
        println(s"${self.path.name} creating child actor")
        val childRef = context.actorOf(Props[Child], name)
        context.become(receiveTellChild(childRef)) // for the next messages it'll only accept TellChild(msg) because the childa already exist
    }

    private def receiveTellChild(ref: ActorRef): Receive = {
      case TellChild(msg) => ref forward msg
    }
  }

  object Parent {
    final case class CreateChild(name: String)
    final case class TellChild(message: String)
  }

  class Child extends Actor {
    override def receive: Receive = {
      case msg => println(s"${self.path} I got: $msg")
    }
  }

  val system = ActorSystem("ParentChild")

  val parent = system.actorOf(Props[Parent], "Parent")

  import Parent._

  parent ! CreateChild("child")

  parent ! TellChild("Hey child!")

  val childSelection = system.actorSelection("/user/Parent/child")
  childSelection ! "I found you"

  system.terminate()

  // actor hierarchies
  // parent -> child -> grandChild -> ...

  /**
   * Guardian actors (top-level actors)
   * - /system = system guardian (manages system actors like logging etc)
   * - /user   = user-level guardian (we create them using system.actorOf(Props[..], ..))
   * - /       = root level guardian (who controls /user and /system level guardians)
   */
}
