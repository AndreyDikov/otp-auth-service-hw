create table if not exists otp_codes (
    id           bigint generated always as identity,
    user_id      bigint       not null,
    operation_id varchar(255) not null,
    code_hash    varchar(255) not null,
    status       varchar(20)  not null,
    expires_at   timestamp    not null,
    created_at   timestamp    not null default now(),
    used_at      timestamp    null,

    constraint pk_otp_codes primary key (id),
    constraint fk_otp_codes_users foreign key (user_id) references users (id) on delete cascade,
    constraint chk_otp_codes_status check (status in ('ACTIVE', 'EXPIRED', 'USED'))
);
