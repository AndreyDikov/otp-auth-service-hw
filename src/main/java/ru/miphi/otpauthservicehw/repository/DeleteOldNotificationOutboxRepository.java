package ru.miphi.otpauthservicehw.repository;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.miphi.otpauthservicehw.entity.request.DeleteOldNotificationOutboxEntityRequest;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.enums.QueryPath.DELETE_OLD_NOTIFICATION_OUTBOX;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DeleteOldNotificationOutboxRepository {

    JdbcClient jdbcClient;

    public int deleteOldNotifications(@Nonnull DeleteOldNotificationOutboxEntityRequest request) {
        return jdbcClient.sql(DELETE_OLD_NOTIFICATION_OUTBOX.sql())
                .param("sent_ttl_days", request.sentTtlDays())
                .param("failed_ttl_days", request.failedTtlDays())
                .update();
    }

}
