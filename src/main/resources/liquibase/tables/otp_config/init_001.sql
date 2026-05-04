create table if not exists otp_config (
    id          smallint  not null default 1,
    code_length int       not null,
    ttl_seconds int       not null,
    updated_at  timestamp not null default now(),

    constraint pk_otp_config primary key (id),
    constraint chk_otp_config_single_row check (id = 1),
    constraint chk_otp_config_code_length check (code_length between 4 and 10),
    constraint chk_otp_config_ttl_seconds check (ttl_seconds > 0)
);
