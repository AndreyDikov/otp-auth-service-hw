select id,
    notification_channel,
    destination,
    encrypted_code,
    attempts
from notification_outbox
where status = 'PENDING'
order by created_at
limit :limit
