
class Parameters(val omegaMin : Double, val omegaMax : Double, val phi1 : Double, val phi2 : Double, val step : Double) {
  def getOmega(iteration : Int) : Double = { scala.math.max(omegaMax - iteration * step, omegaMin) }
  def getPhi1: Double = { phi1 }
  def getPhi2: Double = { phi2 }
}
