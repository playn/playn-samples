libraryDependencies += "com.samskivert" % "sbt-pom-util" % "0.6-SNAPSHOT"

// this wires up JRebel; start game with JRebel via: java/re-start
addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.1")
