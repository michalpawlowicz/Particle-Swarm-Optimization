import akka.actor.{ActorSystem, Props}

object Main extends App {

  val particlesCount = 2
  val iterMax = 100
  val fn = (x: Vector[Double]) => x.sum
  val endCondition = (iteration : Int, fitness : Double) => iteration > iterMax
  val domain : Domain = new Domain(1, 10)
  val parameters : Parameters = new Parameters(1.0, 1.0, 1.0, 1.0, 1.0)
  val dimension = 10

  val system = ActorSystem("HelloSystem")
  val swarm = system.actorOf(Props(new SwarmActor(particlesCount, fn, endCondition, domain, parameters, dimension), name = "helloactor")
}