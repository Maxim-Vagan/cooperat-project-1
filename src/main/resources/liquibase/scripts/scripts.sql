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
    constraint shelter_id_pkey
    primary key,
    name    text,
    name_of_director text,
    address text,
    schedule text
);

-- changeset mkachalov:2
create table if not exists visitor
(
    id      integer
    constraint visitor_id_pkey
    primary key,
    name    text,
    phone_number text,
    email   text,
    chat_id integer,
    status  text
);

-- changeset mkachalov:3
alter table visitor add need_callback boolean;

-- changeset maxvagan:2
create table if not exists pet
(
    id BIGSERIAL NOT NULL,
    pet_name varchar(255),
    animal_kind varchar(255),
    animal_gender varchar(255),
    age integer,
    current_state varchar(255),
    CONSTRAINT pet_pkey PRIMARY KEY (id)
);