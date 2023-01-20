--liquibase formatted sql

-- changeset dmitriy:1
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'volunteer'
CREATE TABLE volunteer
(
    id        BIGSERIAL primary key,
    chat_id   BIGINT,
    name      TEXT,
    last_name TEXT
);

-- changeset dmitriy:3
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'animal'
CREATE TABLE animal
(
    id           BIGSERIAL primary key,
    animal_type  VARCHAR(3),
    animal_name  TEXT,
    volunteer_id BIGINT REFERENCES volunteer (id)
);

-- changeset mabelod:4
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'user_data'
CREATE TABLE user_data
(
    id           BIGSERIAL primary key,
    chat_id      BIGINT,
    name         TEXT,
    phone_number TEXT,
    shelter      INT,
    date         TIMESTAMP,
    animal_id    BIGINT REFERENCES animal (id)
);

-- changeset aleksandr:5
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'photo_of_animal'
CREATE TABLE photo_of_animal
(
    id         BIGSERIAL primary key,
    file_path  TEXT,
    file_size  BIGINT,
    media_type TEXT,
    data       BYTEA
);

-- changeset dmitriy:6
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'report'
CREATE TABLE report
(
    id                 BIGSERIAL primary key,
    date               TIMESTAMP,
    diet               TEXT,
    health             TEXT,
    behavior_change    TEXT,
    user_id            BIGINT REFERENCES user_data (id),
    photo_of_animal_id BIGINT REFERENCES photo_of_animal (id)
);

-- changeset aleksandr:7
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'parameters'
CREATE TABLE parameters
(
    chat_id  BIGINT primary key,
    shelter INT,
    chat INT,
    report INT,
    text TEXT,
    photo_of_animal_id BIGINT REFERENCES photo_of_animal (id)
);



