update otp_codes
set status = 'EXPIRED'
where id = :id
    and status = 'ACTIVE'
