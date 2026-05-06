package ru.miphi.otpauthservicehw.job;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.miphi.otpauthservicehw.client.NotificationClient;
import ru.miphi.otpauthservicehw.client.resolver.NotificationClientResolver;
import ru.miphi.otpauthservicehw.entity.request.MarkNotificationOutboxFailedEntityRequest;
import ru.miphi.otpauthservicehw.entity.response.NotificationOutboxEntityResponse;
import ru.miphi.otpauthservicehw.properties.NotificationOutboxJobProperties;
import ru.miphi.otpauthservicehw.repository.NotificationOutboxRepository;
import ru.miphi.otpauthservicehw.security.OtpCodeCryptoProvider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.miphi.otpauthservicehw.enums.NotificationChannel.EMAIL;

@ExtendWith(MockitoExtension.class)
class NotificationOutboxJobTest {

    private static final Long NOTIFICATION_ID = 1L;
    private static final Integer BATCH_SIZE = 10;
    private static final Integer MAX_ATTEMPTS = 3;
    private static final Integer MAX_ERROR_MESSAGE_LENGTH = 20;

    private static final String DESTINATION = "user@example.com";
    private static final String ENCRYPTED_CODE = "encrypted-code";
    private static final String CODE = "123456";

    @Mock
    NotificationOutboxRepository notificationOutboxRepository;

    @Mock
    NotificationClientResolver notificationClientResolver;

    @Mock
    OtpCodeCryptoProvider otpCodeCryptoProvider;

    @Mock
    NotificationOutboxJobProperties properties;

    @Mock
    NotificationClient notificationClient;

    @InjectMocks
    NotificationOutboxJob notificationOutboxJob;

    @Nested
    class ProcessOutboxTest {

        @Test
        @DisplayName("если pending-уведомлений нет, метод не отправляет уведомления")
        void pendingNotificationsNotFound_shouldDoNothing() {
            when(properties.batchSize())
                    .thenReturn(BATCH_SIZE);
            when(notificationOutboxRepository.getPendingNotifications(BATCH_SIZE))
                    .thenReturn(List.of());

            notificationOutboxJob.processOutbox();

            verify(notificationOutboxRepository).getPendingNotifications(BATCH_SIZE);
            verify(otpCodeCryptoProvider, never()).decrypt(ENCRYPTED_CODE);
            verify(notificationClientResolver, never()).resolve(EMAIL);
            verify(notificationOutboxRepository, never()).markSent(NOTIFICATION_ID);
        }

        @Test
        @DisplayName("если уведомление успешно отправлено, метод помечает его отправленным")
        void notificationSent_shouldMarkSent() {
            when(properties.batchSize())
                    .thenReturn(BATCH_SIZE);
            when(notificationOutboxRepository.getPendingNotifications(BATCH_SIZE))
                    .thenReturn(List.of(buildNotification()));
            when(otpCodeCryptoProvider.decrypt(ENCRYPTED_CODE))
                    .thenReturn(CODE);
            when(notificationClientResolver.resolve(EMAIL))
                    .thenReturn(notificationClient);

            notificationOutboxJob.processOutbox();

            verify(notificationOutboxRepository).getPendingNotifications(BATCH_SIZE);
            verify(otpCodeCryptoProvider).decrypt(ENCRYPTED_CODE);
            verify(notificationClientResolver).resolve(EMAIL);
            verify(notificationClient).sendCode(DESTINATION, CODE);
            verify(notificationOutboxRepository).markSent(NOTIFICATION_ID);
            verify(notificationOutboxRepository, never())
                    .markFailed(org.mockito.ArgumentMatchers.any());
        }

        @Test
        @DisplayName("если расшифровка кода завершилась ошибкой, метод помечает уведомление неуспешным")
        void codeDecryptFailed_shouldMarkFailed() {
            IllegalStateException exception = new IllegalStateException("ошибка расшифровки");

            when(properties.batchSize())
                    .thenReturn(BATCH_SIZE);
            when(properties.maxAttempts())
                    .thenReturn(MAX_ATTEMPTS);
            when(properties.maxErrorMessageLength())
                    .thenReturn(MAX_ERROR_MESSAGE_LENGTH);
            when(notificationOutboxRepository.getPendingNotifications(BATCH_SIZE))
                    .thenReturn(List.of(buildNotification()));
            when(otpCodeCryptoProvider.decrypt(ENCRYPTED_CODE))
                    .thenThrow(exception);

            notificationOutboxJob.processOutbox();

            ArgumentCaptor<MarkNotificationOutboxFailedEntityRequest> captor =
                    ArgumentCaptor.forClass(MarkNotificationOutboxFailedEntityRequest.class);

            verify(notificationOutboxRepository).markFailed(captor.capture());

            MarkNotificationOutboxFailedEntityRequest request = captor.getValue();

            assertEquals(NOTIFICATION_ID, request.id());
            assertEquals(MAX_ATTEMPTS, request.maxAttempts());
            assertEquals("ошибка расшифровки", request.errorMessage());

            verify(notificationClientResolver, never()).resolve(EMAIL);
            verify(notificationClient, never()).sendCode(DESTINATION, CODE);
            verify(notificationOutboxRepository, never()).markSent(NOTIFICATION_ID);
        }

        @Test
        @DisplayName("если отправка уведомления завершилась ошибкой, метод помечает уведомление неуспешным")
        void notificationSendFailed_shouldMarkFailed() {
            IllegalStateException exception = new IllegalStateException("ошибка отправки");

            when(properties.batchSize())
                    .thenReturn(BATCH_SIZE);
            when(properties.maxAttempts())
                    .thenReturn(MAX_ATTEMPTS);
            when(properties.maxErrorMessageLength())
                    .thenReturn(MAX_ERROR_MESSAGE_LENGTH);
            when(notificationOutboxRepository.getPendingNotifications(BATCH_SIZE))
                    .thenReturn(List.of(buildNotification()));
            when(otpCodeCryptoProvider.decrypt(ENCRYPTED_CODE))
                    .thenReturn(CODE);
            when(notificationClientResolver.resolve(EMAIL))
                    .thenReturn(notificationClient);
            doThrow(exception)
                    .when(notificationClient)
                    .sendCode(DESTINATION, CODE);

            notificationOutboxJob.processOutbox();

            ArgumentCaptor<MarkNotificationOutboxFailedEntityRequest> captor =
                    ArgumentCaptor.forClass(MarkNotificationOutboxFailedEntityRequest.class);

            verify(notificationOutboxRepository).markFailed(captor.capture());

            MarkNotificationOutboxFailedEntityRequest request = captor.getValue();

            assertEquals(NOTIFICATION_ID, request.id());
            assertEquals(MAX_ATTEMPTS, request.maxAttempts());
            assertEquals("ошибка отправки", request.errorMessage());

            verify(notificationOutboxRepository, never()).markSent(NOTIFICATION_ID);
        }

        @Test
        @DisplayName("если сообщение ошибки слишком длинное, метод обрезает его")
        void errorMessageTooLong_shouldTrimErrorMessage() {
            String errorMessage = "очень длинное сообщение ошибки";
            IllegalStateException exception = new IllegalStateException(errorMessage);

            when(properties.batchSize())
                    .thenReturn(BATCH_SIZE);
            when(properties.maxAttempts())
                    .thenReturn(MAX_ATTEMPTS);
            when(properties.maxErrorMessageLength())
                    .thenReturn(MAX_ERROR_MESSAGE_LENGTH);
            when(notificationOutboxRepository.getPendingNotifications(BATCH_SIZE))
                    .thenReturn(List.of(buildNotification()));
            when(otpCodeCryptoProvider.decrypt(ENCRYPTED_CODE))
                    .thenThrow(exception);

            notificationOutboxJob.processOutbox();

            ArgumentCaptor<MarkNotificationOutboxFailedEntityRequest> captor =
                    ArgumentCaptor.forClass(MarkNotificationOutboxFailedEntityRequest.class);

            verify(notificationOutboxRepository).markFailed(captor.capture());

            assertEquals(
                    errorMessage.substring(0, MAX_ERROR_MESSAGE_LENGTH),
                    captor.getValue().errorMessage()
            );
        }

    }

    private static NotificationOutboxEntityResponse buildNotification() {
        return NotificationOutboxEntityResponse.builder()
                .id(NOTIFICATION_ID)
                .notificationChannel(EMAIL)
                .destination(DESTINATION)
                .encryptedCode(ENCRYPTED_CODE)
                .attempts(0)
                .build();
    }

}
