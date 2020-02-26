package mapso

import scala.collection.immutable.Vector

class Domain(val lowerBound : Int, val higherBound : Int) {
  def feasible(v : Vector[Double]) : Boolean = { v.forall(d => d > lowerBound && d < higherBound) }
}
