ThisBuild / version      := "0.1"
ThisBuild / scalaVersion := "2.13.1"
lazy val mapso = (project in file("."))
  .settings(
    name := "Particle-Swarm-Optymization-Scala"
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.3"
    libraryDependencies += "com.typesafe" % "config" % "1.3.3"
  )

