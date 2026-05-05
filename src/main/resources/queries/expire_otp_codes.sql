update otp_codes
set status = 'EXPIRED'
where status = 'ACTIVE'
    and expires_at <= now()
