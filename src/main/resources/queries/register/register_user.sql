insert into users (
    login,
    password_hash,
    role
)
values (
    :login,
    :password_hash,
    :role
)
returning id,
    login,
    role
