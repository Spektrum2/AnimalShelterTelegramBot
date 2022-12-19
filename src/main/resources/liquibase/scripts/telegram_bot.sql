--liquibase formatted sql

-- changeset mabelod:1
CREATE TABLE "user"
(
    id              BIGSERIAL primary key ,
    id_chat         BIGINT,
    login           TEXT,
    contact_details TEXT
);

