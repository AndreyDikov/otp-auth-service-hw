select id,
    login,
    role,
    created_at
from users
where role <> 'ADMIN'
order by id
