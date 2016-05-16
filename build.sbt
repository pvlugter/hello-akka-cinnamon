name := "hello-akka"

version := "1.0"

scalaVersion := "2.11.8"

lazy val akkaVersion = "2.4.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "junit" % "junit" % "4.12" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

resolvers += "lightbend-monitoring" at "https://lightbend.bintray.com/commercial-monitoring"

libraryDependencies ++= Seq(
  "com.lightbend.cinnamon" %% "cinnamon-chmetrics" % "2.0.0-M3",
  "com.lightbend.cinnamon" %% "cinnamon-chmetrics-jvm-metrics" % "2.0.0-M3"
)

// Add the agent to a separate configuration so it doesn't add to the normal class path
val Agent = config("agent").hide
val agent = taskKey[Option[File]]("Jar file for the cinnamon agent")
ivyConfigurations += Agent
libraryDependencies += "com.lightbend.cinnamon" % "cinnamon-agent" % "2.0.0-M3" % Agent.name
agent := update.value.matching(moduleFilter(name = "cinnamon-agent")).headOption

// We need to fork the execution to add the java agent
fork in run := true
fork in Test := true
javaOptions in run ++= agent.value.toSeq.map("-javaagent:" + _)
javaOptions in Test ++= agent.value.toSeq.map("-javaagent:" + _)
