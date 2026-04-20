CREATE SEQUENCE IF NOT EXISTS location_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS price_multiplier_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS privilege_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS role_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE app_user
(
    uuid         UUID         NOT NULL,
    username     VARCHAR(255),
    email        VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255),
    password     VARCHAR(255),
    role_id      BIGINT,
    status       VARCHAR(255) NOT NULL,
    CONSTRAINT pk_appuser PRIMARY KEY (uuid)
);

CREATE TABLE app_user_locations
(
    app_user_uuid UUID   NOT NULL,
    locations_id  BIGINT NOT NULL
);

CREATE TABLE location
(
    id        BIGINT       NOT NULL,
    name      VARCHAR(255) NOT NULL,
    shortname VARCHAR(255) NOT NULL,
    is_active BOOLEAN      NOT NULL,
    CONSTRAINT pk_location PRIMARY KEY (id)
);

CREATE TABLE member
(
    uuid         UUID    NOT NULL,
    email        VARCHAR(255),
    name         VARCHAR(255),
    surname      VARCHAR(255),
    phone_number VARCHAR(255),
    monthly_fee  INTEGER,
    location_id  BIGINT,
    comment      VARCHAR(255),
    is_active    BOOLEAN NOT NULL,
    CONSTRAINT pk_member PRIMARY KEY (uuid)
);

CREATE TABLE payment
(
    uuid           UUID                        NOT NULL,
    amount         DECIMAL                     NOT NULL,
    payment_method SMALLINT,
    payment_type   SMALLINT,
    month          date,
    time_stamp     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    payer_in_uuid  UUID                        NOT NULL,
    payer_uuid     UUID                        NOT NULL,
    location_id    BIGINT                      NOT NULL,
    CONSTRAINT pk_payment PRIMARY KEY (uuid)
);

CREATE TABLE price_multiplier
(
    id          BIGINT NOT NULL,
    location_id BIGINT,
    month       date,
    multiplier  DECIMAL,
    CONSTRAINT pk_pricemultiplier PRIMARY KEY (id)
);

CREATE TABLE privilege
(
    id   BIGINT       NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_privilege PRIMARY KEY (id)
);

CREATE TABLE refresh_token
(
    uuid          UUID                        NOT NULL,
    app_user_uuid UUID,
    token         VARCHAR(255)                NOT NULL,
    expiry_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_refreshtoken PRIMARY KEY (uuid)
);

CREATE TABLE role
(
    id   BIGINT       NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_role PRIMARY KEY (id)
);

CREATE TABLE roles_privileges
(
    privilege_id BIGINT NOT NULL,
    role_id      BIGINT NOT NULL
);

CREATE TABLE verification_token
(
    uuid          VARCHAR(255)                NOT NULL,
    token         VARCHAR(255)                NOT NULL,
    app_user_uuid UUID,
    type          VARCHAR(255)                NOT NULL,
    expires_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    used          BOOLEAN,
    CONSTRAINT pk_verificationtoken PRIMARY KEY (uuid)
);

ALTER TABLE app_user
    ADD CONSTRAINT uc_appuser_email UNIQUE (email);

ALTER TABLE app_user
    ADD CONSTRAINT uc_appuser_phonenumber UNIQUE (phone_number);

ALTER TABLE privilege
    ADD CONSTRAINT uc_privilege_name UNIQUE (name);

ALTER TABLE refresh_token
    ADD CONSTRAINT uc_refreshtoken_token UNIQUE (token);

ALTER TABLE role
    ADD CONSTRAINT uc_role_name UNIQUE (name);

ALTER TABLE verification_token
    ADD CONSTRAINT uc_verificationtoken_token UNIQUE (token);

ALTER TABLE app_user
    ADD CONSTRAINT FK_APPUSER_ON_ROLE FOREIGN KEY (role_id) REFERENCES role (id);

ALTER TABLE member
    ADD CONSTRAINT FK_MEMBER_ON_LOCATION FOREIGN KEY (location_id) REFERENCES location (id);

ALTER TABLE payment
    ADD CONSTRAINT FK_PAYMENT_ON_LOCATION FOREIGN KEY (location_id) REFERENCES location (id);

ALTER TABLE payment
    ADD CONSTRAINT FK_PAYMENT_ON_PAYERIN_UUID FOREIGN KEY (payer_in_uuid) REFERENCES app_user (uuid);

ALTER TABLE payment
    ADD CONSTRAINT FK_PAYMENT_ON_PAYER_UUID FOREIGN KEY (payer_uuid) REFERENCES member (uuid);

ALTER TABLE price_multiplier
    ADD CONSTRAINT FK_PRICEMULTIPLIER_ON_LOCATION FOREIGN KEY (location_id) REFERENCES location (id);

ALTER TABLE refresh_token
    ADD CONSTRAINT FK_REFRESHTOKEN_ON_APPUSER_UUID FOREIGN KEY (app_user_uuid) REFERENCES app_user (uuid);

ALTER TABLE verification_token
    ADD CONSTRAINT FK_VERIFICATIONTOKEN_ON_APPUSER_UUID FOREIGN KEY (app_user_uuid) REFERENCES app_user (uuid);

ALTER TABLE app_user_locations
    ADD CONSTRAINT fk_appuseloc_on_app_user FOREIGN KEY (app_user_uuid) REFERENCES app_user (uuid);

ALTER TABLE app_user_locations
    ADD CONSTRAINT fk_appuseloc_on_location FOREIGN KEY (locations_id) REFERENCES location (id);

ALTER TABLE roles_privileges
    ADD CONSTRAINT fk_rolpri_on_privilege FOREIGN KEY (privilege_id) REFERENCES privilege (id);

ALTER TABLE roles_privileges
    ADD CONSTRAINT fk_rolpri_on_role FOREIGN KEY (role_id) REFERENCES role (id);