name := "Lift 3.0 starter template"

version := "0.1.0"

organization := "eu.brosbit"

scalaVersion := "2.13.4"

resolvers ++= Seq(
  "snapshots"     at "https://oss.sonatype.org/content/repositories/snapshots",
  "staging"       at "https://oss.sonatype.org/content/repositories/staging",
  "releases"      at "https://oss.sonatype.org/content/repositories/releases"
)

enablePlugins(JettyPlugin)

Test / unmanagedResourceDirectories += baseDirectory.value / "src/main/webapp"

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  val liftVersion = "3.5.0"
  Seq(
    "net.liftweb"       %% "lift-webkit"            % liftVersion,
    "net.liftweb" 	%% "lift-mapper" 	% liftVersion % "compile",
    "net.liftweb" 	%% "lift-actor" 	% liftVersion % "compile",
    //"net.liftmodules" % "lift-jquery-module_3.3_2.12" % "2.10",
    "net.liftweb" %% "lift-mongodb" % liftVersion % "compile",
    "org.postgresql" % "postgresql" % "42.2.5",
    "com.sun.xml.messaging.saaj" % "saaj-impl" % "2.0.1",
    "org.jsoup" % "jsoup" % "1.8.3",
    "ch.qos.logback"    % "logback-classic"         % "1.2.3",
    "javax.servlet"     % "javax.servlet-api"       % "3.0.1"            % "provided"
  )
}

Test / scalacOptions ++= Seq("-Yrangepos")
