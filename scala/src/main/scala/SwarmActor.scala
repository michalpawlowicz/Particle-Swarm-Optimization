import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorRef, Props}

class SwarmActor(val particlesCount: Int) extends Actor {
  val actors: List[ActorRef] = null;
  var finalSolution: FinalSolution = new FinalSolution(new Information(null, Double.MaxValue));
  var receivedFinalInformation: AtomicInteger = new AtomicInteger()

  def this(graph : String,
           particlesCount : Int,
           fn : Vector[Double] => Double,
           endCondition : (Int, Double) => Boolean,
           domain: Domain,
           parameters: Parameters,
           dimension: Int) {
    this(particlesCount)

    val actors = for (_ <- 0 until particlesCount) yield
      context.actorOf(Props(
        new ParticleActor(
          endCondition,
          new Particle(dimension, domain, parameters, fn))))

    GraphParser.parse(graph).lazyZip(actors)
        .foreach((representation, particleActor) => {
          particleActor ! new InitAcquaintances(
            actors.lazyZip(actors.indices).filter(
              (_, index) => {
                representation.contains(index)
              }
            ).map(p => p._1).toList
          )
        })
  }

  override def receive: Receive = {
    case initAcquaintancesResponse: InitAcquaintancesResponse => {
      if (initAcquaintancesResponse.response) {
        sender() ! new Start()
      }
    }

    case msg: FinalSolution => {
      val receivedMessages = receivedFinalInformation.addAndGet(1);

      if (msg.information.fitness < this.finalSolution.information.fitness) {
        this.finalSolution = msg
      }

      if (receivedMessages == this.particlesCount) {
        context.stop(self)
        context.system.terminate()
      }
    }
    case _ => println("SwarmActor huh?")
  }
}

