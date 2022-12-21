--liquibase formatted sql

-- changeset dmitriy:1
CREATE TABLE "report"
(
    id              BIGSERIAL primary key ,
    "name"          TEXT,
    diet            TEXT,
    health          TEXT,
    behavior_change TEXT

);