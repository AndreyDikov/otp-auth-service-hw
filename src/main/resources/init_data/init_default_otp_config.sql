insert into otp_config (
    id,
    code_length,
    ttl_seconds
)
values (
    1,
    :code_length,
    :ttl_seconds
)
on conflict (id) do nothing
