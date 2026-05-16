delete from notification_outbox
where (status = 'SENT'
           and processed_at <= now() - (:sent_ttl_days * interval '1 day'))
    or (status = 'FAILED'
            and processed_at <= now() - (:failed_ttl_days * interval '1 day'))
