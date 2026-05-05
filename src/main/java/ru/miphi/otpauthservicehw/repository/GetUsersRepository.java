package ru.miphi.otpauthservicehw.repository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.miphi.otpauthservicehw.entity.response.GetUsersEntityResponse;
import ru.miphi.otpauthservicehw.enums.UserRole;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.repository.paths.QueryPath.GET_USERS;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetUsersRepository {

    JdbcClient jdbcClient;

    public List<GetUsersEntityResponse> getUsers() {
        return jdbcClient.sql(GET_USERS.sql())
                .query((rs, rowNum) -> GetUsersEntityResponse.builder()
                        .id(rs.getLong("id"))
                        .login(rs.getString("login"))
                        .role(UserRole.valueOf(rs.getString("role")))
                        .build()
                )
                .list();
    }

}
