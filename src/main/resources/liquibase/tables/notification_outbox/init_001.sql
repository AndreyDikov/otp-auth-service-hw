create table if not exists notification_outbox (
    id                   bigint generated always as identity,
    notification_channel varchar(20)  not null,
    destination          varchar(255) not null,
    encrypted_code       text         not null,
    status               varchar(20)  not null default 'PENDING',
    attempts             int          not null default 0,
    error_message        text         null,
    created_at           timestamp    not null default now(),
    processed_at         timestamp    null,

    constraint pk_notification_outbox primary key (id),
    constraint chk_notification_outbox_channel check (notification_channel in ('EMAIL', 'SMS', 'TELEGRAM', 'FILE')),
    constraint chk_notification_outbox_status check (status in ('PENDING', 'SENT', 'FAILED')),
    constraint chk_notification_outbox_attempts check (attempts >= 0)
);
