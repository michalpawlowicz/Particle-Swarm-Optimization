import akka.actor.ActorRef

import scala.collection.immutable.Vector

class InitAcquaintances(val acquaintances: List[ActorRef]) {}
class InitAcquaintancesResponse(val response : Boolean) {}
class Start() {}
class InformOthers() {}
class Information() {}
class IterateRequest(val iteration : Int, val gBest : Vector[Double]) {}
class IterateResponse(val solution: Option[(Vector[Double], Double)]) {}

