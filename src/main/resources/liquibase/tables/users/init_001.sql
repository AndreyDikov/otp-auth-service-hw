create table if not exists users (
    id            bigint generated always as identity,
    login         varchar(100)   not null,
    password_hash varchar(255)   not null,
    role          user_role_enum not null,
    created_at    timestamp      not null default now(),

    constraint pk_users primary key (id),
    constraint uq_users_login unique (login)
);
