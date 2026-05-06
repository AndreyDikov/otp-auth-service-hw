insert into notification_outbox (
    notification_channel,
    destination,
    encrypted_code
)
values (
    :notification_channel,
    :destination,
    :encrypted_code
)
