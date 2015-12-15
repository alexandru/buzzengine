lazy val scalaV = "2.11.7"

lazy val buzzengine = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
      "org.flywaydb" % "flyway-core" % "3.2.1",
      // for RDBMS access
      "com.typesafe.slick" %% "slick" % "3.1.0",
      "org.postgresql" % "postgresql" % "9.4-1201-jdbc4"
    ),
    // Heroku specific
    herokuAppName in Compile := "buzzengine",
    herokuSkipSubProjects in Compile := false,
    routesGenerator := InjectedRoutesGenerator
  )
