package buzzengine.services.rdbms

import java.sql.Timestamp
import java.util.UUID

import org.joda.time.{LocalDateTime, DateTimeZone, DateTime}
import slick.driver.JdbcDriver

final class DBSchema(val driver: JdbcDriver) {
  import driver.profile.api._

  /** Mapping for Joda's DateTime */
  implicit def dateTimeMapping = MappedColumnType.base[DateTime, java.sql.Timestamp](
    dt => new Timestamp(dt.getMillis),
    ts => new LocalDateTime(ts.getTime).toDateTime(DateTimeZone.UTC))

  case class ArticleRow(
    uuid: UUID,
    url: String,
    title: Option[String],
    isDeleted: Boolean,
    createdAt: DateTime,
    updatedAt: DateTime)

  class ArticlesTable(tag: Tag) extends Table[ArticleRow](tag, "buzz_articles") {
    def uuid = column[UUID]("uuid", O.PrimaryKey)
    def url = column[String]("url")
    def title = column[Option[String]]("title")
    def isDeleted = column[Boolean]("is_deleted")
    def createdAt = column[DateTime]("created_at")
    def updatedAt = column[DateTime]("updated_at")

    def urlIdx = index("articles_url_idx", url, unique = true)
    def selectIdx = index("articles_select_idx", (isDeleted, createdAt))

    def * = {
      (uuid, url, title, isDeleted, createdAt, updatedAt) <>
        (ArticleRow.tupled, ArticleRow.unapply)
    }
  }

  object ArticlesTable {
    val query = TableQuery[ArticlesTable]

    /** Select only active articles */
    def active = query.filter(_.isDeleted === false)

    /** DDL for creating the table */
    def create = query.schema.create
    /** DDL for dropping the table */
    def drop = query.schema.drop
  }

  case class PersonaRow(
    uuid: UUID,
    name: String,
    email: Option[String],
    website: Option[String],
    isDeleted: Boolean,
    createdAt: DateTime,
    updatedAt: DateTime)

  class PersonasTable(tag: Tag) extends Table[PersonaRow](tag, "buzz_personas") {
    def uuid = column[UUID]("uuid", O.PrimaryKey)
    def name = column[String]("name")
    def email = column[Option[String]]("email")
    def website = column[Option[String]]("website")

    def isDeleted = column[Boolean]("is_deleted")
    def createdAt = column[DateTime]("created_at")
    def updatedAt = column[DateTime]("updated_at")

    def selectIdx = index("personas_select_idx", (isDeleted, createdAt))

    def * = {
      (uuid, name, email, website, isDeleted, createdAt, updatedAt) <>
        (PersonaRow.tupled, PersonaRow.unapply)
    }
  }

  object PersonasTable {
    val query = TableQuery[PersonasTable]

    /** Select only active articles */
    def active = query.filter(_.isDeleted === false)

    /** DDL for creating the table */
    def create = query.schema.create
    /** DDL for dropping the table */
    def drop = query.schema.drop
  }

  case class CommentRow(
    uuid: UUID,
    articleUUID: UUID,
    personaUUID: UUID,
    replyToCommentUUID: Option[UUID],
    remoteIPAddress: Option[String],
    text: String,
    isDeleted: Boolean,
    createdAt: DateTime,
    updatedAt: DateTime)

  class CommentsTable(tag: Tag) extends Table[CommentRow](tag, "buzz_comments") {
    def uuid = column[UUID]("uuid", O.PrimaryKey)
    def articleUUID = column[UUID]("article_uuid")
    def personaUUID = column[UUID]("persona_uuid")
    def replyToCommentUUID = column[Option[UUID]]("reply_to_uuid")
    def remoteIPAddress = column[Option[String]]("remote_ip_address")
    def text = column[String]("text")

    def isDeleted = column[Boolean]("is_deleted")
    def createdAt = column[DateTime]("created_at")
    def updatedAt = column[DateTime]("updated_at")

    def article = foreignKey("article_fk", articleUUID, ArticlesTable.query)(_.uuid)
    def persona = foreignKey("persona_fk", personaUUID, PersonasTable.query)(_.uuid)

    def * = {
      (uuid, articleUUID, personaUUID, replyToCommentUUID, remoteIPAddress, text, isDeleted, createdAt, updatedAt) <>
        (CommentRow.tupled, CommentRow.unapply)
    }
  }

  object CommentsTable {
    val query = TableQuery[CommentsTable]

    /** DDL for creating the table */
    def create = query.schema.create
    /** DDL for dropping the table */
    def drop = query.schema.drop
  }
}