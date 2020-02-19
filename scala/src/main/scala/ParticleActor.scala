import akka.actor.{Actor, ActorRef, Props}

import scala.collection.immutable.Vector

class ParticleActor(val endCondition : (Int, Double) => Boolean,
                    var gBestSolution : Vector[Double],
                    var gBestFitness : Double) extends Actor {

  private val communicationActor : ActorRef = context.actorOf(Props[CommunicationActor])
  private var workerActor : ActorRef = _
  private var iteration = 0

  def this(endCondition : (Int, Double) => Boolean, particle: Particle) = {
    this(endCondition, particle.position, particle.apply())
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
      if(msg.solution.isDefined) {
        if(msg.solution.get._2 < gBestFitness) {
          gBestFitness = msg.solution.get._2
          gBestSolution = msg.solution.get._1
          println("New best solution: " + gBestFitness)
          informationRequest() // Inform others about new best solution
        }
      }
      if (!endCondition(iteration, gBestFitness)) {
        iteration = iteration + 1
        iterationRequest()
      } else {
        // signal end of work
      }
    }
    case msg: Information => {
      if(msg.fitness < gBestFitness) {
        gBestFitness = msg.fitness
        gBestSolution = msg.solution
      }
    }
    case _ => println("ParticleActor huh?")
  }

  def informationRequest(): Unit = {
    communicationActor ! new InformOthers(this.gBestSolution, this.gBestFitness)
  }
  def iterationRequest(): Unit = {
    workerActor ! new IterateRequest(this.iteration, this.gBestSolution)
  }
}
