ThisBuild / version      := "0.1"
ThisBuild / scalaVersion := "2.13.1"

lazy val mapso = (project in file("."))
  .settings(
    name := "Multi-Agent-Particle-Swarm-Optymization"
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.3"
    libraryDependencies += "com.typesafe" % "config" % "1.3.3"
  )

lazy val mapso_benchmark = (project in file("."))
  .settings(
    name := "Multi-Agent-Particle-Swarm-Optymization-Benchmark"
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.3"
    libraryDependencies += "com.typesafe" % "config" % "1.3.3"
  )

