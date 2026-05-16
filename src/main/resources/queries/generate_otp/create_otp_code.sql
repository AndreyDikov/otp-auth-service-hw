insert into otp_codes (user_id,
    operation_id,
    code_hash,
    status,
    expires_at
)
values (:user_id,
    :operation_id,
    :code_hash,
    'ACTIVE',
    :expires_at
)
