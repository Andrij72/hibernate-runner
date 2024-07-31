CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(256) UNIQUE,
    firstname  VARCHAR(256),
    lastname   VARCHAR(128),
    birth_date DATE,
    role       VARCHAR(32),
    info       JSONB,
    company_id INT REFERENCES company (id)

);

CREATE TABLE company
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL UNIQUE
);

CREATE TABLE profile
(
    id       BIGSERIAL PRIMARY KEY,
    user_id  BIGINT NOT NULL UNIQUE REFERENCES users (id),
    language VARCHAR(64),
    street   VARCHAR(128)
);

CREATE TABLE chat
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL UNIQUE
);

CREATE TABLE users_chat
(
    id         BIGSERIAL PRIMARY KEY,
    users_id   BIGINT REFERENCES users (id),
    chat_id    BIGINT REFERENCES chat (id),
    created_at TIMESTAMP    NOT NULL,
    created_by VARCHAR(256) NOT NULL,
    UNIQUE (users_id, chat_id)
);

CREATE TABLE company_locale
(
    company_id  INT  NOT NULL REFERENCES company (id),
    lang  CHAR(6) NOT NULL,
    description VARCHAR(256) NOT NULL,
    PRIMARY KEY (company_id, lang)
);

drop table company CASCADE;
drop table users CASCADE;
drop table profile CASCADE;
drop table users_chat;
drop table company_locale;
drop table chat;

