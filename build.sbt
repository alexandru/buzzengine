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
      // depending on all supported database drivers
      "org.postgresql" % "postgresql" % "9.4-1201-jdbc4",
      "mysql" % "mysql-connector-java" % "5.1.38",
      "org.mongodb.scala" %% "mongo-scala-driver" % "1.1.0"
    ),
    // Heroku specific
    herokuAppName in Compile := "buzzengine",
    herokuSkipSubProjects in Compile := false,
    routesGenerator := InjectedRoutesGenerator
  )
