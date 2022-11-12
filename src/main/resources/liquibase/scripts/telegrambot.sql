-- liquibase formatted sql

-- changeset maxvagan:1
CREATE TABLE notification_task
(
        id BIGSERIAL NOT NULL,
        telegram_chat_id BIGINT,
        message_text VARCHAR(255),
        message_datetime timestamp without time zone,
        send_datetime timestamp without time zone,
        CONSTRAINT notification_task_id_pkey PRIMARY KEY (id)
);