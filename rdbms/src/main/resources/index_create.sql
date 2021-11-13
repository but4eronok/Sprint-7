--liquibase formatted sql

--changeset but4eronok:index

create index new_index on account1(id);