package ru.miphi.otpauthservicehw.repository;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.miphi.otpauthservicehw.entity.request.RegisterEntityRequest;
import ru.miphi.otpauthservicehw.entity.response.RegisterEntityResponse;
import ru.miphi.otpauthservicehw.enums.UserRole;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.repository.paths.QueryPath.*;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RegisterRepository {

    JdbcClient jdbcClient;

    public boolean existsUserByLogin(String login) {
        return jdbcClient.sql(EXISTS_USER_BY_LOGIN.sql())
                .param("login", login)
                .query(Boolean.class)
                .single();
    }

    public boolean existsAdmin() {
        return jdbcClient.sql(EXISTS_ADMIN.sql())
                .query(Boolean.class)
                .single();
    }

    public RegisterEntityResponse registerUser(@Nonnull RegisterEntityRequest request) {
        return jdbcClient.sql(REGISTER_USER.sql())
                .param("login", request.login())
                .param("password_hash", request.passwordHash())
                .param("role", request.role().name())
                .query((rs, rowNum) -> RegisterEntityResponse.builder()
                        .id(rs.getLong("id"))
                        .login(rs.getString("login"))
                        .role(UserRole.valueOf(rs.getString("role")))
                        .build()
                )
                .single();
    }

}
