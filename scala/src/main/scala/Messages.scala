import akka.actor.ActorRef

import scala.collection.immutable.Vector

class InitAcquaintances(val acquaintances: List[ActorRef]) {}
class InitAcquaintancesResponse(val response : Boolean) {}
class Start() {}
class InformOthers() {}
class Information() {}
class IterateRequest() {}
class IterateResponse(val solution: Option[Vector[Double]]) {}

