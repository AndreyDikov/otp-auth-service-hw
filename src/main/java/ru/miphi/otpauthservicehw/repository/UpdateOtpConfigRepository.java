package ru.miphi.otpauthservicehw.repository;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.miphi.otpauthservicehw.entity.request.UpdateOtpConfigEntityRequest;
import ru.miphi.otpauthservicehw.entity.response.UpdateOtpConfigEntityResponse;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.repository.paths.QueryPath.UPDATE_OTP_CONFIG;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UpdateOtpConfigRepository {

    JdbcClient jdbcClient;

    public Optional<UpdateOtpConfigEntityResponse> updateOtpConfig(
            @Nonnull UpdateOtpConfigEntityRequest request
    ) {
        return jdbcClient.sql(UPDATE_OTP_CONFIG.sql())
                .param("code_length", request.codeLength())
                .param("ttl_seconds", request.ttlSeconds())
                .query((rs, rowNum) -> UpdateOtpConfigEntityResponse.builder()
                        .id(rs.getShort("id"))
                        .codeLength(rs.getInt("code_length"))
                        .ttlSeconds(rs.getInt("ttl_seconds"))
                        .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                        .build()
                )
                .optional();
    }

}
