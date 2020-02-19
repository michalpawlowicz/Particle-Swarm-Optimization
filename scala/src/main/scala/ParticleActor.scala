import akka.actor.{Actor, ActorRef, Props}

import scala.collection.immutable.Vector

class ParticleActor(val endCondition : (Int, Double) => Boolean,
                    var gBestSolution : Vector[Double]) extends Actor {

  private val communicationActor : ActorRef = context.actorOf(Props[CommunicationActor])
  private var workerActor : ActorRef = null

  def this(endCondition : (Int, Double) => Boolean, particle: Particle) = {
    this(endCondition, particle.position)
    this.workerActor = context.actorOf(Props(new WorkerActor(particle)));
  }

  override def receive: Receive = {
    case msg: InitAcquaintances => {
      println("Received acquaintances")
      this.communicationActor.forward(msg)
    }
    case _: Start => {
      println("Start working")
      iterationRequest()
      informationRequest()
    }
    case msg: IterateResponse => {
      println("Got response from worker")
      if(msg.solution.isDefined) {
        // update best solution
        informationRequest()
      }
      if (!endCondition(0, )) {
        iterationRequest()
      } else {
        // signal end of work
      }
    }
    case _ => println("ParticleActor huh?")
  }

  def informationRequest(): Unit = {
    communicationActor ! new InformOthers()
  }
  def iterationRequest(): Unit = {
    workerActor ! new IterateRequest()
  }
}
