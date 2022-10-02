package part2actors.child

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object WordCounterImmutable extends scala.App {

  case class Initialize(nChildren: Int)
  case class WordCountTask(id: Int, text: String)
  case class WordCountReply(id: Int, count: Int)

  class WordCountMaster extends Actor {

    override def receive: Receive = {
      case Initialize(nChildren) =>
        println("[master] initializing...")
        val childrenRefs = for (i <- 1 to nChildren) yield context.actorOf(Props[WordCounterWorker], s"worker_$i")
        context.become(withChildren(childrenRefs, 0, 0, Map.empty))
    }

    private def withChildren(childrenRefs: Seq[ActorRef], currentChildIndex: Int, currentTaskId: Int, requestMap: Map[Int, ActorRef]): Receive = {

      case text: String =>
        println(s"[master] I have received: $text - I will send it to child $currentChildIndex")
        val originalSender = sender()
        val task = WordCountTask(currentTaskId, text)
        val childRef = childrenRefs(currentChildIndex)
        childRef ! task
        val nextChildIndex = (currentChildIndex + 1) % childrenRefs.length
        val nextTaskId     = currentTaskId + 1
        val newRequestMap  = requestMap + (currentTaskId -> originalSender)
        context.become(withChildren(childrenRefs, nextChildIndex, nextTaskId, newRequestMap))


      case WordCountReply(id, count) =>
        println(s"[master] I have received a reply for task id $id with count $count")
        val originalSender = requestMap(id)
        originalSender ! count
        val newRequestMap = requestMap - id
        context.become(withChildren(childrenRefs, currentChildIndex, currentTaskId, newRequestMap))

    }

  }

  class WordCounterWorker extends Actor {
    override def receive: Receive = {
      case WordCountTask(id, text) =>
        println(s"${self.path} I have received a task $id with $text")
        sender ! WordCountReply(id, text.split(" ").length)
    }
  }



  val system = ActorSystem("System")

  val master = system.actorOf(Props[WordCountMaster], "master")

  master ! Initialize(10)

  master ! "Hello there xd xd"
  master ! "Boom boom"
  master ! "whatt whattt???"

}
