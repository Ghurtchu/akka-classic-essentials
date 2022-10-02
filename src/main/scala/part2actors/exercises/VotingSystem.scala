package part2actors.exercises

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object VotingSystem extends scala.App {

  final case class Vote(candidate: String) // Mark citizen as having voted for some candidate
  final case class VoteStatusReply(candidate: Option[String])
  case object VoteStatusRequest

  // Citizen votes
  class Citizen extends Actor {

    var candidate: Option[String] = None

    override def receive: Receive = {
      case Vote(c) => candidate = Some(c)
      case VoteStatusRequest => sender() ! VoteStatusReply(candidate)
    }

  }

  final case class AggregateVotes(citizens: Set[ActorRef])

  // Aggerates the votes submitted by citizens
  class VoteAggregator extends Actor {

    var stillWaiting: Set[ActorRef] = Set.empty
    var currentStats: Map[String, Int] = Map.empty

    override def receive: Receive = {
      case AggregateVotes(citizens) => {
        stillWaiting = citizens
        citizens.foreach(_ ! VoteStatusRequest)
      }
      case VoteStatusReply(None) => {
         // a citizen has not voted yet
        sender() ! VoteStatusRequest
      }
      case VoteStatusReply(Some(candidate)) => {
        var newStillWaiting = stillWaiting - sender()
        val currentVotesOfCandidate = currentStats.getOrElse(candidate, 0)
        currentStats = currentStats + (candidate -> (currentVotesOfCandidate + 1))
        if (newStillWaiting.isEmpty) println(s"stats: $currentStats")
        else stillWaiting = newStillWaiting
      }

    }
  }

  val system = ActorSystem("VotingSystem")

  val alice   = system.actorOf(Props[Citizen])
  val bob     = system.actorOf(Props[Citizen])
  val charlie = system.actorOf(Props[Citizen])
  val daniel  = system.actorOf(Props[Citizen])

  alice ! Vote("Martin")
  bob ! Vote("Jonas")
  charlie ! Vote("Roland")
  daniel ! Vote("Roland")

  val voteAggregator = system.actorOf(Props[VoteAggregator])
  voteAggregator ! AggregateVotes(Set(alice, bob, charlie, daniel))

  /*
  * Print the status of the votes
  * Map[Candidate, VoteCount] => Map[String, Int]
  * martin = 1
  * jonas = 1
  * roland = 2
  * */

}
