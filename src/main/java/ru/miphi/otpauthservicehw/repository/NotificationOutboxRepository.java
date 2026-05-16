package ru.miphi.otpauthservicehw.repository;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.miphi.otpauthservicehw.entity.request.MarkNotificationOutboxFailedEntityRequest;
import ru.miphi.otpauthservicehw.entity.response.NotificationOutboxEntityResponse;
import ru.miphi.otpauthservicehw.enums.NotificationChannel;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.enums.QueryPath.*;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class NotificationOutboxRepository {

    JdbcClient jdbcClient;

    public List<NotificationOutboxEntityResponse> getPendingNotifications(Integer limit) {
        return jdbcClient.sql(GET_PENDING_NOTIFICATION_OUTBOX.sql())
                .param("limit", limit)
                .query((rs, rowNum) -> NotificationOutboxEntityResponse.builder()
                        .id(rs.getLong("id"))
                        .notificationChannel(NotificationChannel.valueOf(rs.getString("notification_channel")))
                        .destination(rs.getString("destination"))
                        .encryptedCode(rs.getString("encrypted_code"))
                        .attempts(rs.getInt("attempts"))
                        .build()
                )
                .list();
    }

    public void markSent(Long id) {
        jdbcClient.sql(MARK_NOTIFICATION_OUTBOX_SENT.sql())
                .param("id", id)
                .update();
    }

    public void markFailed(@Nonnull MarkNotificationOutboxFailedEntityRequest request) {
        jdbcClient.sql(MARK_NOTIFICATION_OUTBOX_FAILED.sql())
                .param("id", request.id())
                .param("max_attempts", request.maxAttempts())
                .param("error_message", request.errorMessage())
                .update();
    }

}
