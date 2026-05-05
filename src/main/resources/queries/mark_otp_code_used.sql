update otp_codes
set status = 'USED',
    used_at = now()
where id = :id
    and status = 'ACTIVE'
