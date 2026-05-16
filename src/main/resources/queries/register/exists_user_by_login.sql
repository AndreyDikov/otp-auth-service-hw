select exists(
    select 1
    from users
    where login = :login
)
