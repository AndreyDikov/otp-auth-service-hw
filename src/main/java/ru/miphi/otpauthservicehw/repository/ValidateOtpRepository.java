package ru.miphi.otpauthservicehw.repository;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.miphi.otpauthservicehw.entity.request.ValidateOtpEntityRequest;
import ru.miphi.otpauthservicehw.entity.response.ValidateOtpEntityResponse;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.repository.paths.QueryPath.*;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ValidateOtpRepository {

    JdbcClient jdbcClient;

    public Optional<ValidateOtpEntityResponse> getActiveOtpCode(@Nonnull ValidateOtpEntityRequest request) {
        return jdbcClient.sql(GET_ACTIVE_OTP_CODE.sql())
                .param("user_id", request.userId())
                .param("operation_id", request.operationId())
                .query((rs, rowNum) -> ValidateOtpEntityResponse.builder()
                        .id(rs.getLong("id"))
                        .codeHash(rs.getString("code_hash"))
                        .expiresAt(rs.getTimestamp("expires_at").toLocalDateTime())
                        .build()
                )
                .optional();
    }

    public int markOtpCodeUsed(Long id) {
        return jdbcClient.sql(MARK_OTP_CODE_USED.sql())
                .param("id", id)
                .update();
    }

    public void markOtpCodeExpired(Long id) {
        jdbcClient.sql(MARK_OTP_CODE_EXPIRED.sql())
                .param("id", id)
                .update();
    }

}
