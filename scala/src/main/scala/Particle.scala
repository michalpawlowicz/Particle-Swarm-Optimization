import scala.collection.immutable.Vector
import scala.util.Random

class Particle(val fn : Vector[Double] => Double,
               val domain : Domain,
               val parameters: Parameters,
               var position: Vector[Double],
               var velocity : Vector[Double],
               var bestKnownPosition : Vector[Double],
               var bestKnownFitness : Double) {

  val random: Random.type = scala.util.Random

  def this(domain : Domain,
           parameters : Parameters,
           fn : Vector[Double] => Double,
           position : Vector[Double],
           velocity : Vector[Double]) = {
    this(fn, domain, parameters, position, velocity, position, fn(position))
  }

  def this(dimension : Int, domain : Domain, parameters : Parameters, fn : Vector[Double] => Double) = {
    this(domain,
      parameters,
      fn,
      randomVector(dimension),
      randomVector(dimension))
  }

  def randomVector(dimension : Int): Vector[Double] = {
    (for (_ <- 0 until dimension) yield random.nextDouble()).toVector
  }

  def apply() : Double = {
    fn(position);
  }

  def updateVelocity(gBest : Vector[Double], omega : Double, phi_1 : Double, phi_2 : Double): Unit = {
    if(!domain.feasible(position)) {
      this.position = this.position.map(d => 0.002)
    } else {
      this.velocity = this.bestKnownPosition
        .lazyZip {
          this.position
        }
        .lazyZip(this.velocity)
        .lazyZip(gBest)
        .map((bestKnownPosition, position, velocity, gBest) => {
          omega * velocity + phi_1 * random.nextDouble() * (bestKnownPosition - position) + phi_2 * random.nextDouble() * (gBest - position)
        })
    }
  }

  def updatePosition(): Unit = {
    this.position = this.position.lazyZip(this.velocity).map((position, velocity) => position + velocity)
  }

  def iterate(gBest : Vector[Double], iteration : Int) : Option[(Vector[Double], Double)] = {
    this.updateVelocity(gBest, parameters.getOmega(iteration), parameters.getPhi1, parameters.getPhi2)
    this.updatePosition()
    if(this.domain.feasible(this.position)) {
      val fitness = this.apply()
      if(fitness < this.bestKnownFitness) {
        this.bestKnownPosition = this.position
        this.bestKnownFitness = fitness
        return Some(Tuple2[Vector[Double], Double](this.bestKnownPosition, this.bestKnownFitness))
      }
    }
    None
  }
}
