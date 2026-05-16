select id,
    login,
    role
from users
where role <> 'ADMIN'
order by id
limit :limit
offset :offset
