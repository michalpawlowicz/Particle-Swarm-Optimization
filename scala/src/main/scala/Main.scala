import akka.actor.{ActorSystem, Props}

object Main extends App {
  val particlesCount = 16
  val dimension = 128
  val iterMax = 3e5
  val fn = (v: Vector[Double]) => 418.9829 * dimension - v.reduce((acc, vi) => acc + vi * scala.math.sin(scala.math.sqrt(scala.math.abs(vi))))
  val endCondition = (iteration : Int, fitness : Double) => iteration > iterMax
  val domain : Domain = new Domain(-500, 500)

  val omegaMax : Double = 1.8
  val omegaMin : Double = 0.1
  val parameters : Parameters = new Parameters(0.4, 5.8, 0.5, 1.5, (omegaMax - omegaMin) / iterMax)

  val system = ActorSystem("HelloSystem")
  val swarm = system.actorOf(Props(new SwarmActor(particlesCount, fn, endCondition, domain, parameters, dimension)))
}