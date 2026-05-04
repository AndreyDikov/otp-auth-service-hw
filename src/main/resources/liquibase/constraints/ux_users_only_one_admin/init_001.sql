create unique index if not exists ux_users_only_one_admin
    on users (role)
    where role = 'ADMIN';
