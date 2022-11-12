-- liquibase formatted sql

-- changeset mkachalov:1
create table shelter
(
    id      integer
        constraint id
            primary key,
    name    text,
    INN text,
    name_of_director text,
    address text,
    working_time text
);

