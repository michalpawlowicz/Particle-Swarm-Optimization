import akka.actor.{Actor, ActorRef}

class CommunicationActor() extends Actor {
  var acquaintances : Option[List[ActorRef]] = None
  override def receive: Receive = {
    case msg: InitAcquaintances => {
      this.acquaintances = Some(msg.acquaintances)
      sender().tell(new InitAcquaintancesResponse(true), context.parent)
    }
    case msg: InformOthers => {
      this.acquaintances.map(acquaintances => acquaintances.foreach(particleRef => particleRef ! new Information(msg.solution, msg.fitness)))
    }
    case _ => println("SlaveActor huh?")
  }
}
