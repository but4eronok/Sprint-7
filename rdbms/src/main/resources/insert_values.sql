--liquibase formatted sql

--changeset but4eronok:insert

insert into account1(amount, version) values (500,0);
insert into account1(amount, version) values (300,0);
insert into account1(amount, version) values (0,0);