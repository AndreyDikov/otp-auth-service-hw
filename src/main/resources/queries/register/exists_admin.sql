select exists(
    select 1
    from users
    where role = 'ADMIN'
)
