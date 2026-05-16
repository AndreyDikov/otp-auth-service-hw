package ru.miphi.otpauthservicehw.repository;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.miphi.otpauthservicehw.entity.request.GetUsersEntityRequest;
import ru.miphi.otpauthservicehw.entity.response.GetUsersEntityResponse;
import ru.miphi.otpauthservicehw.enums.UserRole;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.enums.QueryPath.COUNT_USERS;
import static ru.miphi.otpauthservicehw.enums.QueryPath.GET_USERS;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetUsersRepository {

    JdbcClient jdbcClient;

    public List<GetUsersEntityResponse> getUsers(@Nonnull GetUsersEntityRequest request) {
        return jdbcClient.sql(GET_USERS.sql())
                .param("limit", request.limit())
                .param("offset", request.offset())
                .query((rs, rowNum) -> GetUsersEntityResponse.builder()
                        .id(rs.getLong("id"))
                        .login(rs.getString("login"))
                        .role(UserRole.valueOf(rs.getString("role")))
                        .build()
                )
                .list();
    }

    public Long countUsers() {
        return jdbcClient.sql(COUNT_USERS.sql())
                .query(Long.class)
                .single();
    }

}
