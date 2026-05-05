package ru.miphi.otpauthservicehw.repository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.miphi.otpauthservicehw.entity.response.LoginEntityResponse;
import ru.miphi.otpauthservicehw.enums.UserRole;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.repository.paths.QueryPath.GET_USER_BY_LOGIN;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LoginRepository {

    JdbcClient jdbcClient;

    public Optional<LoginEntityResponse> getUserByLogin(String login) {
        return jdbcClient.sql(GET_USER_BY_LOGIN.sql())
                .param("login", login)
                .query((rs, rowNum) -> LoginEntityResponse.builder()
                        .id(rs.getLong("id"))
                        .login(rs.getString("login"))
                        .passwordHash(rs.getString("password_hash"))
                        .role(UserRole.valueOf(rs.getString("role")))
                        .build()
                )
                .optional();
    }

}
