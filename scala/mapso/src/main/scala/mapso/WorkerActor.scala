package mapso

import akka.actor.Actor

class WorkerActor(val particle: Particle) extends Actor {
  override def receive: Receive = {
    case msg: IterateRequest => sender() ! new IterateResponse(this.particle.iterate(msg.gBest, msg.iteration))
    case _ => println("WorkerActor huh?")
  }
}
