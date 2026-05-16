create sequence location_seq start with 1 increment by 50;
create sequence price_multiplier_seq start with 1 increment by 50;
create sequence privilege_seq start with 1 increment by 50;
create sequence role_seq start with 1 increment by 50;
create table app_user
(
    role_id      bigint,
    uuid         uuid         not null,
    email        varchar(255) not null unique,
    password     varchar(255),
    phone_number varchar(255) unique,
    status       varchar(255) not null check ((status in ('PENDING', 'ACTIVE', 'DISABLED'))),
    username     varchar(255),
    primary key (uuid)
);
create table app_user_locations
(
    locations_id  bigint not null,
    app_user_uuid uuid   not null
);
create table location
(
    is_active boolean      not null,
    id        bigint       not null,
    name      varchar(255) not null,
    shortname varchar(255) not null,
    primary key (id)
);
create table member
(
    is_active    boolean not null,
    monthly_fee  integer,
    location_id  bigint,
    uuid         uuid    not null,
    comment      varchar(255),
    email        varchar(255),
    name         varchar(255),
    phone_number varchar(255),
    surname      varchar(255),
    primary key (uuid)
);
create table payment
(
    amount         numeric(38, 2) not null,
    month          date,
    payment_method smallint check ((payment_method between 0 and 1)),
    payment_type   smallint check ((payment_type between 0 and 1)),
    location_id    bigint         not null,
    time_stamp     timestamp(6)   not null,
    payer_in_uuid  uuid           not null,
    payer_uuid     uuid           not null,
    uuid           uuid           not null,
    primary key (uuid)
);
create table price_multiplier
(
    month       date,
    multiplier  numeric(38, 2),
    id          bigint not null,
    location_id bigint,
    primary key (id)
);
create table privilege
(
    id   bigint       not null,
    name varchar(255) not null unique,
    primary key (id)
);
create table refresh_token
(
    expiry_date   timestamp(6) with time zone not null,
    app_user_uuid uuid,
    uuid          uuid                        not null,
    token         varchar(255)                not null unique,
    primary key (uuid)
);
create table role
(
    id   bigint       not null,
    name varchar(255) not null unique,
    primary key (id)
);
create table roles_privileges
(
    privilege_id bigint not null,
    role_id      bigint not null
);
create table verification_token
(
    used          boolean,
    expires_at    timestamp(6) not null,
    app_user_uuid uuid,
    token         varchar(255) not null unique,
    type          varchar(255) not null check ((type in ('REGISTRATION', 'PASSWORD_RESET'))),
    uuid          varchar(255) not null,
    primary key (uuid)
);
alter table if exists app_user
    add constraint FK49hx9nj6onfot1fxtonj986ab foreign key (role_id) references role;
alter table if exists app_user_locations
    add constraint FK3t5cpuufe7ye0wivfjxb2vgm6 foreign key (locations_id) references location;
alter table if exists app_user_locations
    add constraint FK5tf2sjchfsomrxbot8odgqudn foreign key (app_user_uuid) references app_user;
alter table if exists member
    add constraint FKrk7x186jf7n5kps1pt2auuexp foreign key (location_id) references location;
alter table if exists payment
    add constraint FK83sg2kyn0xhfu5qm0647hbdpi foreign key (location_id) references location;
alter table if exists payment
    add constraint FKbxh8k4oqps3l39ge2uq8coucd foreign key (payer_uuid) references member;
alter table if exists payment
    add constraint FKe1pksihd9jcs2qnsbflsee19s foreign key (payer_in_uuid) references app_user;
alter table if exists price_multiplier
    add constraint FKmqhf5b43v35b1e241n6flgsd foreign key (location_id) references location;
alter table if exists refresh_token
    add constraint FKq6iyrqirpesy9i2hh6ept9ylh foreign key (app_user_uuid) references app_user;
alter table if exists roles_privileges
    add constraint FK5yjwxw2gvfyu76j3rgqwo685u foreign key (privilege_id) references privilege;
alter table if exists roles_privileges
    add constraint FK9h2vewsqh8luhfq71xokh4who foreign key (role_id) references role;
alter table if exists verification_token
    add constraint FKennx04ougtt2217i8taeymp6t foreign key (app_user_uuid) references app_user;
