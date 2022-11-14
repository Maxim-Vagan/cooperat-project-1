-- liquibase formatted sql

-- changeset maxvagan:1
create table if not exists notification_task
(
    id BIGSERIAL NOT NULL,
    telegram_chat_id bigint,
    message_text text,
    message_datetime timestamp without time zone,
    send_datetime timestamp without time zone,
    CONSTRAINT notification_task_id_pkey PRIMARY KEY (id)
);

-- changeset mkachalov:1
create table if not exists shelter
(
    id      integer
        constraint id
            primary key,
    name    text,
    name_of_director text,
    address text,
    schedule text
);

-- changeset mkachalov:2
/*create table if not exists visitor
(
    id      integer
    constraint id
    primary key,
    name    text,
    phone_number text,
    email text,
    chat_id integer,
    status text
);*/

-- changeset maxvagan:2
create table if not exists pet
(
    id BIGSERIAL NOT NULL,
    petName varchar(255),
    animalKind varchar(255),
    animalGender varchar(255),
    age integer,
    currentState varchar(255),
    pathFileToPhoto varchar(255),
    CONSTRAINT pet_pkey PRIMARY KEY (id)
);