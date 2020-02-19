import akka.actor.{Actor, ActorRef}

class CommunicationActor() extends Actor {
  var acquaintances : Option[List[ActorRef]] = None
  override def receive: Receive = {
    case msg: InitAcquaintances => {
      println("Acquaintances saved: " + msg.acquaintances.size)
      this.acquaintances = Some(msg.acquaintances)
      sender().tell(new InitAcquaintancesResponse(true), context.parent)
    }
    case _: InformOthers => {
      println("Inform others")
      this.acquaintances.map(acquaintances => acquaintances.foreach(particleRef => particleRef ! new Information()))
    }
    case _ => println("SlaveActor huh?")
  }
}
