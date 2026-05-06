package ru.miphi.otpauthservicehw.repository;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.miphi.otpauthservicehw.entity.request.CreateNotificationOutboxEntityRequest;
import ru.miphi.otpauthservicehw.entity.request.CreateOtpCodeEntityRequest;
import ru.miphi.otpauthservicehw.entity.response.GenerateOtpConfigEntityResponse;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.repository.paths.QueryPath.*;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GenerateOtpRepository {

    JdbcClient jdbcClient;

    public Optional<GenerateOtpConfigEntityResponse> getOtpConfig() {
        return jdbcClient.sql(GET_OTP_CONFIG.sql())
                .query((rs, rowNum) -> GenerateOtpConfigEntityResponse.builder()
                        .codeLength(rs.getInt("code_length"))
                        .ttlSeconds(rs.getInt("ttl_seconds"))
                        .build()
                )
                .optional();
    }

    public void createOtpCode(@Nonnull CreateOtpCodeEntityRequest request) {
        jdbcClient.sql(CREATE_OTP_CODE.sql())
                .param("user_id", request.userId())
                .param("operation_id", request.operationId())
                .param("code_hash", request.codeHash())
                .param("expires_at", request.expiresAt())
                .update();
    }

    public void createNotificationOutbox(@Nonnull CreateNotificationOutboxEntityRequest request) {
        jdbcClient.sql(CREATE_NOTIFICATION_OUTBOX.sql())
                .param("notification_channel", request.notificationChannel().name())
                .param("destination", request.destination())
                .param("encrypted_code", request.encryptedCode())
                .update();
    }

}
