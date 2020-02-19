import akka.actor.{ActorSystem, Props}

object Main extends App {
  val particlesCount = 20
  val dimension = 32
  val iterMax = 3e4
  val fn = (v: Vector[Double]) => 418.9829 * dimension - v.reduce((acc, vi) => acc + vi * scala.math.sin(scala.math.sqrt(scala.math.abs(vi))))
  val endCondition = (iteration : Int, fitness : Double) => iteration > iterMax
  val domain : Domain = new Domain(-500, 500)
  val parameters : Parameters = new Parameters(0.4, 1.8, 0.7, 1.5, 1e-2)

  val system = ActorSystem("HelloSystem")
  val swarm = system.actorOf(Props(new SwarmActor(particlesCount, fn, endCondition, domain, parameters, dimension)))
}