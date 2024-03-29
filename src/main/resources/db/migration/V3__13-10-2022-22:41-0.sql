CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS addresses_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE addresses
(
    id                      BIGINT NOT NULL,
    created_date_time       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_date_time TIMESTAMP WITHOUT TIME ZONE,
    street                  VARCHAR(255),
    postal_code             VARCHAR(255),
    city                    VARCHAR(255),
    state                   VARCHAR(255),
    country                 VARCHAR(255),
    CONSTRAINT pk_address PRIMARY KEY (id)
);

ALTER TABLE users
    ADD COLUMN created_date_time TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE users
    ADD COLUMN last_modified_date_time TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE users
    ADD COLUMN address_id BIGINT;

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ADDRESS FOREIGN KEY (address_id) REFERENCES addresses (id);

ALTER TABLE persons
    ADD COLUMN created_date_time TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE persons
    ADD COLUMN last_modified_date_time TIMESTAMP WITHOUT TIME ZONE;
