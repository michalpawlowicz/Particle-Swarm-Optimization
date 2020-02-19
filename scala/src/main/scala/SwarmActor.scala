import akka.actor.{Actor, ActorRef, Props}

class SwarmActor extends Actor {

  val actors : List[ActorRef] = null;

  def this(particlesCount : Int,
           fn : Vector[Double] => Double,
           endCondition : (Int, Double) => Boolean,
           domain: Domain,
           parameters: Parameters,
           dimension : Int) {
    this()

    println("Swarm initialization")

    val actors = for (_ <- 0 until particlesCount) yield
      context.actorOf(Props(
        new ParticleActor(
          endCondition,
          new Particle(dimension, domain, parameters, fn))))

    println("Sending acquaintances to particles")

    actors.foreach(particleActor => { particleActor ! new InitAcquaintances(List()) })
  }

  override def receive: Receive = {
    case initAcquaintancesResponse: InitAcquaintancesResponse => {
      if (initAcquaintancesResponse.response) {
        sender() ! new Start()
      }
    }
    case _ => println("SwarmActor huh?")
  }
}

