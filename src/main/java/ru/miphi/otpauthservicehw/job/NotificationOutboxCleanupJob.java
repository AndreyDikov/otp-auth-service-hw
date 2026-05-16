package ru.miphi.otpauthservicehw.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.miphi.otpauthservicehw.entity.request.DeleteOldNotificationOutboxEntityRequest;
import ru.miphi.otpauthservicehw.properties.NotificationOutboxCleanupJobProperties;
import ru.miphi.otpauthservicehw.repository.DeleteOldNotificationOutboxRepository;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class NotificationOutboxCleanupJob {

    DeleteOldNotificationOutboxRepository deleteOldNotificationOutboxRepository;

    NotificationOutboxCleanupJobProperties properties;

    @Scheduled(
            fixedDelayString = "${app.job.notification-outbox-cleanup.fixed-delay-ms}",
            initialDelayString = "${app.job.notification-outbox-cleanup.initial-delay-ms}"
    )
    public void deleteOldNotifications() {
        int deletedRows = deleteOldNotificationOutboxRepository.deleteOldNotifications(
                DeleteOldNotificationOutboxEntityRequest.builder()
                        .sentTtlDays(properties.sentTtlDays())
                        .failedTtlDays(properties.failedTtlDays())
                        .build()
        );

        if (deletedRows > 0) {
            log.info("Количество удалённых старых записей outbox: {}", deletedRows);
        }
    }

}
