package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActorsExercises extends App {

  // Distributed Word counting

  object WordCounterMaster {
    case class Initialize(nChildren: Int)
    case class WordCountTask(id: Int, text: String)
    case class WordCountReply(id: Int, count: Int)
  }
  class WordCounterMaster extends Actor {
    import WordCounterMaster._
    override def receive: Receive = {
      case Initialize(nChildren) =>
        println("[master] initializing...")
        val childrenRefs = for (i <- 1 to nChildren) yield context.actorOf(Props[WordCounterWorker], s"wcw_$i")
        context.become(withChildren(childrenRefs, 0, 0, Map()))
    }
    def withChildren(childrenRefs: Seq[ActorRef], currentChildIndex: Int, currentTaskId: Int, requestMap: Map[Int, ActorRef]): Receive = {
      case text: String =>
        println(s"[master] I have received: $text - I will send it to child $currentChildIndex")
        val originalSender = sender()
        val task = WordCountTask(currentTaskId, text)
        val childRef = childrenRefs(currentChildIndex)
        childRef ! task
        val nextChildIndex = (currentChildIndex + 1) % childrenRefs.length
        val newTaskId = currentTaskId + 1
        val newRequestMap = requestMap + (currentTaskId -> originalSender)
        context.become(withChildren(childrenRefs, nextChildIndex, newTaskId, newRequestMap))
      case WordCountReply(id, count) =>
        println(s"[master] I have received a reply for task id $id with $count")
        val orginalSender = requestMap(id)
        orginalSender ! count
        context.become(withChildren(childrenRefs, currentChildIndex, currentTaskId, requestMap - id))
    }
  }

  class WordCounterWorker extends Actor {
    import WordCounterMaster._
    override def receive: Receive = {
      case WordCountTask(id, text) =>
        println(s"${self.path} I have received a task $id with $text")
        sender() ! WordCountReply(id, text.split(" ").length)
    }
  }

  class TestActor extends Actor {
    import WordCounterMaster._
    override def receive: Receive = {
      case "go" =>
        val master = context.actorOf(Props[WordCounterMaster], "master")
        master ! Initialize(3)
        val text = List("I love Akka", "Scala is super dope", "yes", "me too")
        text.foreach(text => master ! text)
      case count: Int =>
        println(s"[test actor] I received a reply: $count")
    }
  }

  val system = ActorSystem("roundRobinWordCountExercise")
  val testActor = system.actorOf(Props[TestActor], "testActor")
  testActor ! "go"

  /*
  create WordCounterMaster
  send Initialize(10) to wordCounterMaster
  send "Akka is awesome" to wordCounterMaster
  wordCounterMaster will sent a WordCountTask("...") to one of its children
  child replies with WordCountReply(3) to the master
  master replies with 3 to the sender

  requester -> wcm -> wcw
  requester <- wcm <- wcw

  round robin logic

   */
}
