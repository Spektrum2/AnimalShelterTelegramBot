--liquibase formatted sql

-- changeset dmitriy:1
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'volunteer'
CREATE TABLE volunteer
(
    id        BIGSERIAL primary key,
    name      TEXT,
    last_name TEXT
);
--changeset aleksandr:2
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_type WHERE typname = 'animal_type'
CREATE TYPE animal_type AS ENUM('DOG', 'CAT');

-- changeset dmitriy:3
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'animal'
CREATE TABLE animal
(
    id           BIGSERIAL primary key,
    animal_type  animal_type,
    animal_name  TEXT,
    volunteer_id BIGINT REFERENCES volunteer (id)
);

-- changeset mabelod:4
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'user_data'
CREATE TABLE user_data
(
    id           BIGSERIAL primary key,
    id_chat      BIGINT,
    name         TEXT,
    phone_number TEXT,
    shelter      INT,
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
    name               TEXT,
    date               TIMESTAMP,
    diet               TEXT,
    health             TEXT,
    behavior_change    TEXT,
    user_id            BIGINT REFERENCES user_data (id),
    photo_of_animal_id BIGINT REFERENCES photo_of_animal (id)
);




