package part2actors.child

import akka.actor.{Actor, ActorRef, Props}

object Exercises extends scala.App {

  // distributed word counting

  object WordCounterMaster {
    final case class Initialize(nChildren: Int) // for creating children workers
    final case class WordCountTask(id: Int, text: String) // for giving each children uniquely identifiable task
    final case class WordCountReply(id: Int, count: Int) // for replying when the job is done
  }

  class WordCounterMaster extends Actor {

    import WordCounterMaster._

    override def receive: Receive = {
      case Initialize(n) =>
        val childrenRefs = for (i <- 1 to n) yield context.actorOf(Props[WordCountWorker], s"wcw_$i")
        context.become(withChildren(childrenRefs, 0, 0, Map.empty))
    }

    def withChildren(
                      childrenRefs: Seq[ActorRef],
                      currentChildIndex: Int,
                      currentTaskId: Int,
                      requestMap: Map[Int, ActorRef]
                    ): Receive = {

      case text: String =>
        val originalSender = sender()
        val task = WordCountTask(currentTaskId, text)
        val childRef = childrenRefs(currentChildIndex)
        childRef ! task
        val nextChildIndex = (currentChildIndex + 1) % childrenRefs.length
        val newTaskId = currentTaskId + 1
        val newRequestMap = requestMap + (currentTaskId -> originalSender)
        context.become(withChildren(childrenRefs, nextChildIndex, newTaskId, newRequestMap))

      case WordCountReply(id, count) =>
          val originalSender = requestMap(id)
          originalSender ! count
          context.become(withChildren(childrenRefs, currentChildIndex, currentTaskId, requestMap - id))
        // problem - who is the original requester of the text? sender()? no, it's just which the last sent the message

    }
  }

  class WordCountWorker extends Actor {

    import WordCounterMaster._

    override def receive: Receive = {
      case WordCountTask(id, text) => sender() ! WordCountReply(id, text.split(" ").length)
    }
  }

  /**
   * create WordCounterMaster
   * send initialize(10) to wordCountMaster
   * send "Akka is awesome" to wordCountMaster:
   * - wcm will send a WordCountTask("...") to one of its children
   * - child replies with WordCountReply(3) [Akka is Awesome]
   * - wordCountMaster replies with 3 to the initial sender
   */

  // requester -> master -> children
  // requester <-  children <-

  // round robin logic
  // 1, 2, 3, 4, 5 and 7 tasks
  //


}
