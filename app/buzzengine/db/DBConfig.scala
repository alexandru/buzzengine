package buzzengine.db

import slick.jdbc.JdbcBackend._
import slick.util.AsyncExecutor

case class DBConfig(
  url: String,
  user: Option[String],
  password: Option[String],
  driver: String,
  numThreads: Int,
  queueSize: Int) {

  lazy val slickDriver = driver match {
    case "org.postgresql.Driver" =>
      Class.forName(driver)
      slick.driver.PostgresDriver
    /*
    case "org.h2.Driver" =>
      Class.forName(driver)
      slick.driver.H2Driver
      */
  }

  lazy val database = {
    Database.forURL(url, user.orNull, password.orNull,
      driver = driver,
      executor = AsyncExecutor("buzz-db", numThreads, queueSize))
  }
}

