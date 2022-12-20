--liquibase formatted sql

-- changeset mabelod:1
CREATE TABLE user_data
(
    id           BIGSERIAL primary key,
    id_chat      BIGINT,
    name        TEXT,
    phone_number TEXT
);



