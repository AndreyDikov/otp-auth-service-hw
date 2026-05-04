package ru.miphi.otpauthservicehw.repository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.miphi.otpauthservicehw.enums.UserRole;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.repository.paths.QueryPath.DELETE_USER;
import static ru.miphi.otpauthservicehw.repository.paths.QueryPath.GET_USER_ROLE_BY_ID;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DeleteUserRepository {

    JdbcClient jdbcClient;

    public Optional<UserRole> findUserRoleById(Long userId) {
        return jdbcClient.sql(GET_USER_ROLE_BY_ID.sql())
                .param("user_id", userId)
                .query(UserRole.class)
                .optional();
    }

    public void deleteUser(Long userId) {
        jdbcClient.sql(DELETE_USER.sql())
                .param("user_id", userId)
                .update();
    }

}
