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
    id      bigint
    constraint visitor_id_pkey
    primary key,
    phone_number text,
    chat_id bigint,
    message_status text,
    sheler_status text
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

-- changeset maxvagan:3
create table if not exists volunteer
(
    id BIGSERIAL NOT NULL,
    name varchar(255),
    surname varchar(255),
    lastname varchar(255),
    phone_number varchar(255),
    CONSTRAINT volunteer_id_pkey PRIMARY KEY (id)
);

-- changeset mkachalov:4
create table if not exists dog_visitor
(
    id      bigint
    constraint dog_visitor_id_pkey
    primary key,
    name    text,
    phone_number text,
    email   text,
    chat_id bigint
);