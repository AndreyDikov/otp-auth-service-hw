update notification_outbox
set status = 'SENT',
    processed_at = now(),
    error_message = null
where id = :id
    and status = 'PROCESSING'
