import java.io.File

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import mapso.{Domain, Parameters, SwarmActor}

object Main extends App {

  val fileName = System.getProperty("confAppName")

  println("Loaded configuration: " +  fileName)
  val config = ConfigFactory.parseFile(new File(fileName))

  val graph = config.getString("graph")
  val particlesCount=config.getString("particlesCount").toInt
  val dimension=config.getString("dimension").toInt
  val iterMax=config.getString("iterMax").toDouble.toInt
  val omegaMin=config.getString("omegaMin").toDouble
  val omegaMax=config.getString("omegaMax").toDouble
  val phi_1=config.getString("phi_1").toDouble
  val phi_2=config.getString("phi_2").toDouble

  val fn = (v: Vector[Double]) => 418.9829 * dimension - v.map(vi => vi * scala.math.sin(scala.math.sqrt(scala.math.abs(vi)))).sum
  val endCondition = (iteration : Int, fitness : Double) => iteration > iterMax
  val domain : Domain = new Domain(-500, 500)

  val parameters : Parameters = new Parameters(omegaMin, omegaMax, phi_1, phi_2, (omegaMax - omegaMin) / iterMax)

  val system = ActorSystem("HelloSystem")
  val swarm = system.actorOf(Props(new SwarmActor(GraphParser.parse(graph), particlesCount, fn, endCondition, domain, parameters, dimension)))
}
