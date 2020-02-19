import akka.actor.{ActorSystem, Props}

object Main extends App {
  val particlesCount = 32
  val dimension = 128
  val iterMax = 3e5
  val fn = (v: Vector[Double]) => 418.9829 * dimension - v.map(vi => vi * scala.math.sin(scala.math.sqrt(scala.math.abs(vi)))).sum
  val endCondition = (iteration : Int, fitness : Double) => iteration > iterMax
  val domain : Domain = new Domain(-500, 500)

  val omegaMax : Double = 1.8
  val omegaMin : Double = 0.1
  val parameters : Parameters = new Parameters(omegaMin, omegaMax, 0.4, 1.8, (omegaMax - omegaMin) / iterMax)

  val system = ActorSystem("HelloSystem")
  val swarm = system.actorOf(Props(new SwarmActor(particlesCount, fn, endCondition, domain, parameters, dimension)))
}