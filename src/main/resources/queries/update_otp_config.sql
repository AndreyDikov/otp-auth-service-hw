update otp_config
set code_length = :code_length,
    ttl_seconds = :ttl_seconds,
    updated_at = now()
where id = 1
returning id,
    code_length,
    ttl_seconds,
    updated_at
