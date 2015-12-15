CREATE TABLE buzz_articles (
  uuid uuid NOT NULL PRIMARY KEY,
  url character varying NOT NULL UNIQUE,
  title character varying,
  is_deleted boolean NOT NULL,
  created_at timestamp without time zone NOT NULL,
  updated_at timestamp without time zone NOT NULL
);

CREATE INDEX articles_select_idx ON buzz_articles
  USING btree (is_deleted, created_at);

CREATE TABLE buzz_personas (
  uuid uuid NOT NULL PRIMARY KEY,
  name character varying NOT NULL,
  email character varying,
  website character varying,
  is_deleted boolean NOT NULL,
  created_at timestamp without time zone NOT NULL,
  updated_at timestamp without time zone NOT NULL
);

CREATE INDEX personas_select_idx ON buzz_personas
  USING btree (is_deleted, created_at);

CREATE TABLE buzz_comments (
  uuid uuid NOT NULL PRIMARY KEY,
  article_uuid uuid NOT NULL,
  persona_uuid uuid NOT NULL,
  reply_to_uuid uuid,
  remote_ip_address character varying,
  text character varying NOT NULL,
  is_deleted boolean NOT NULL,
  created_at timestamp without time zone NOT NULL,
  updated_at timestamp without time zone NOT NULL
);

ALTER TABLE ONLY buzz_comments
  ADD CONSTRAINT article_fk FOREIGN KEY (article_uuid) REFERENCES buzz_articles(uuid);

ALTER TABLE ONLY buzz_comments
  ADD CONSTRAINT persona_fk FOREIGN KEY (persona_uuid) REFERENCES buzz_personas(uuid);


