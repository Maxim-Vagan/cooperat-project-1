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
create table if not exists dog
(
    id BIGSERIAL NOT NULL,
    pet_name varchar(255),
    animal_gender varchar(255),
    age integer,
    current_state varchar(255),
    path_file_to_photo varchar(255),
    CONSTRAINT pet_pkey PRIMARY KEY (id)
);

-- changeset maxvagan:3
create table if not exists volunteer
(
    id SERIAL NOT NULL,
    name varchar(255),
    surname varchar(255),
    lastname varchar(255),
    phone_number varchar(255),
    CONSTRAINT volunteer_id_pkey PRIMARY KEY (id)
);

-- changeset maxvagan:4
alter table dog add column pet_id bigint unique;

-- changeset maxvagan:5
create table if not exists try_period_registry
(
    id BIGSERIAL NOT NULL,
    pet_id bigint REFERENCES pets_and_shelters(pet_id),
    visitor_id integer REFERENCES visitors_and_shelters(visitor_id),
    volunteer_id integer REFERENCES volunteer(id),
    try_period_status_id varchar(255) DEFAULT 'ACTIVE',
    start_date timestamp without time zone,
    end_date timestamp without time zone,
    additional_period_end_date timestamp without time zone,
    reason_description text,
    shelter_id integer REFERENCES shelter(id)
    CONSTRAINT tryperiod_id_pkey PRIMARY KEY (id)
);

-- changeset maxvagan:6
create table if not exists visitors_and_shelters
(
    id BIGSERIAL NOT NULL,
    visitor_id integer REFERENCES dog_visitor(id),
    shelter_id integer REFERENCES shelter(id),
    CONSTRAINT vas_id_pkey PRIMARY KEY (id)
);
create table if not exists pets_and_shelters
(
    id BIGSERIAL NOT NULL,
    pet_id bigint REFERENCES dog(pet_id),
    shelter_id integer REFERENCES shelter(id),
    CONSTRAINT pas_id_pkey PRIMARY KEY (id)
);
create table if not exists daily_report
(
    id BIGSERIAL NOT NULL,
    pet_id bigint,
    shelter_id integer,
    create_date timestamp without time zone,
    delete_date timestamp without time zone,
    file_size bigint,
    media_type varchar(255),
    photo bytea,
    path_file_to_photo varchar(255),
    day_diet text,
    main_health varchar(255),
    old_hebits text,
    new_hebits text,
    CONSTRAINT report_id_pkey PRIMARY KEY (id)
);

-- changeset maxvagan:7
create table if not exists cat
(
    id BIGSERIAL NOT NULL,
    pet_name varchar(255),
    animal_gender varchar(255),
    age integer,
    current_state varchar(255),
    path_file_to_photo varchar(255),
    pet_id bigint unique,
    CONSTRAINT cat_pkey PRIMARY KEY (id)
);
create table if not exists cat_visitor
(
    id bigint
    constraint cat_visitor_id_pkey
    primary key,
    name text,
    phone_number text,
    email text,
    chat_id bigint
);
-- changeset maxvagan:8
alter table dog add column shelter_id integer REFERENCES shelter(id);
alter table cat add column shelter_id integer REFERENCES shelter(id);