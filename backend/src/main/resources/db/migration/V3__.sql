CREATE SEQUENCE IF NOT EXISTS member_category_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE member_categories
(
    member_uuid   UUID   NOT NULL,
    categories_id BIGINT NOT NULL
);

CREATE TABLE member_category
(
    id        BIGINT NOT NULL,
    name      VARCHAR(255),
    shortname VARCHAR(255),
    CONSTRAINT pk_membercategory PRIMARY KEY (id)
);

ALTER TABLE location
    ADD CONSTRAINT uc_location_name UNIQUE (name);

ALTER TABLE location
    ADD CONSTRAINT uc_location_shortname UNIQUE (shortname);

ALTER TABLE member_categories
    ADD CONSTRAINT uc_member_categories_categories UNIQUE (categories_id);

ALTER TABLE member_category
    ADD CONSTRAINT uc_membercategory_name UNIQUE (name);

ALTER TABLE member_category
    ADD CONSTRAINT uc_membercategory_shortname UNIQUE (shortname);

ALTER TABLE member_categories
    ADD CONSTRAINT fk_memcat_on_member FOREIGN KEY (member_uuid) REFERENCES member (uuid);

ALTER TABLE member_categories
    ADD CONSTRAINT fk_memcat_on_member_category FOREIGN KEY (categories_id) REFERENCES member_category (id);