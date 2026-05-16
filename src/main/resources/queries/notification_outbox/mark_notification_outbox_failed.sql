update notification_outbox
set status = case
        when attempts + 1 >= :max_attempts then 'FAILED'
        else 'PENDING'
    end,
    attempts = attempts + 1,
    error_message = :error_message,
    processed_at = case
        when attempts + 1 >= :max_attempts then now()
    end
where id = :id
    and status = 'PROCESSING'
