with claimed as (
    select id
    from notification_outbox
    where status = 'PENDING'
    order by created_at
    limit :limit
        for update skip locked
)
update notification_outbox
set status = 'PROCESSING'
from claimed
where notification_outbox.id = claimed.id
returning notification_outbox.id,
    notification_outbox.notification_channel,
    notification_outbox.destination,
    notification_outbox.encrypted_code,
    notification_outbox.attempts
