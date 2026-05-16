package ru.miphi.otpauthservicehw.job;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.miphi.otpauthservicehw.entity.request.DeleteOldNotificationOutboxEntityRequest;
import ru.miphi.otpauthservicehw.properties.NotificationOutboxCleanupJobProperties;
import ru.miphi.otpauthservicehw.repository.DeleteOldNotificationOutboxRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationOutboxCleanupJobTest {

    private static final Integer SENT_TTL_DAYS = 7;
    private static final Integer FAILED_TTL_DAYS = 30;

    @Mock
    DeleteOldNotificationOutboxRepository deleteOldNotificationOutboxRepository;

    @Mock
    NotificationOutboxCleanupJobProperties properties;

    @InjectMocks
    NotificationOutboxCleanupJob notificationOutboxCleanupJob;

    @Nested
    class DeleteOldNotificationsTest {

        @Test
        @DisplayName("если старые записи outbox найдены, метод удаляет их")
        void oldNotificationsFound_shouldDeleteOldNotifications() {
            when(properties.sentTtlDays())
                    .thenReturn(SENT_TTL_DAYS);
            when(properties.failedTtlDays())
                    .thenReturn(FAILED_TTL_DAYS);
            when(deleteOldNotificationOutboxRepository.deleteOldNotifications(any()))
                    .thenReturn(5);

            notificationOutboxCleanupJob.deleteOldNotifications();

            ArgumentCaptor<DeleteOldNotificationOutboxEntityRequest> captor =
                    ArgumentCaptor.forClass(DeleteOldNotificationOutboxEntityRequest.class);

            verify(deleteOldNotificationOutboxRepository).deleteOldNotifications(captor.capture());

            DeleteOldNotificationOutboxEntityRequest request = captor.getValue();

            assertEquals(SENT_TTL_DAYS, request.sentTtlDays());
            assertEquals(FAILED_TTL_DAYS, request.failedTtlDays());
        }

        @Test
        @DisplayName("если старые записи outbox не найдены, метод не падает")
        void oldNotificationsNotFound_shouldDoNothing() {
            when(properties.sentTtlDays())
                    .thenReturn(SENT_TTL_DAYS);
            when(properties.failedTtlDays())
                    .thenReturn(FAILED_TTL_DAYS);
            when(deleteOldNotificationOutboxRepository.deleteOldNotifications(any()))
                    .thenReturn(0);

            notificationOutboxCleanupJob.deleteOldNotifications();

            ArgumentCaptor<DeleteOldNotificationOutboxEntityRequest> captor =
                    ArgumentCaptor.forClass(DeleteOldNotificationOutboxEntityRequest.class);

            verify(deleteOldNotificationOutboxRepository).deleteOldNotifications(captor.capture());

            DeleteOldNotificationOutboxEntityRequest request = captor.getValue();

            assertEquals(SENT_TTL_DAYS, request.sentTtlDays());
            assertEquals(FAILED_TTL_DAYS, request.failedTtlDays());
        }

    }

}
