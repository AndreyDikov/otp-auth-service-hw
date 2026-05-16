select id,
    code_hash,
    expires_at
from otp_codes
where user_id = :user_id
    and operation_id = :operation_id
    and status = 'ACTIVE'
order by created_at desc
limit 1
