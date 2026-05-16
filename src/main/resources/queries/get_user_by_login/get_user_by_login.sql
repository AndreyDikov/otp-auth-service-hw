select id,
    login,
    password_hash,
    role
from users
where login = :login
