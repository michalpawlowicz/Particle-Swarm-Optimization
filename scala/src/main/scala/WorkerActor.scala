import akka.actor.Actor

class WorkerActor(val particle: Particle) extends Actor {
  override def receive: Receive = {
    case _: IterateRequest => {
      println("Working..")
      sender() ! new IterateResponse(None)
    }
    case _ => println("WorkerActor huh?")
  }
}
