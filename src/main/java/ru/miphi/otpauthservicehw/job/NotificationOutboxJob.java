package ru.miphi.otpauthservicehw.job;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.miphi.otpauthservicehw.client.resolver.NotificationClientResolver;
import ru.miphi.otpauthservicehw.entity.request.MarkNotificationOutboxFailedEntityRequest;
import ru.miphi.otpauthservicehw.entity.response.NotificationOutboxEntityResponse;
import ru.miphi.otpauthservicehw.properties.NotificationOutboxJobProperties;
import ru.miphi.otpauthservicehw.repository.NotificationOutboxRepository;
import ru.miphi.otpauthservicehw.security.OtpCodeCryptoProvider;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class NotificationOutboxJob {

    NotificationOutboxRepository notificationOutboxRepository;

    NotificationClientResolver notificationClientResolver;
    OtpCodeCryptoProvider otpCodeCryptoProvider;

    NotificationOutboxJobProperties properties;

    @Scheduled(
            fixedDelayString = "${app.job.notification-outbox.fixed-delay-ms}",
            initialDelayString = "${app.job.notification-outbox.initial-delay-ms}"
    )
    public void processOutbox() {
        notificationOutboxRepository.claimPendingNotifications(properties.batchSize())
                .forEach(this::processNotification);
    }

    private void processNotification(NotificationOutboxEntityResponse notification) {
        try {
            String code = otpCodeCryptoProvider.decrypt(notification.encryptedCode());

            notificationClientResolver.resolve(notification.notificationChannel())
                    .sendCode(notification.destination(), code);

            notificationOutboxRepository.markSent(notification.id());
        } catch (Exception exception) {
            log.warn("Не удалось отправить OTP код из outbox, id={}", notification.id(), exception);

            notificationOutboxRepository.markFailed(MarkNotificationOutboxFailedEntityRequest.builder()
                    .id(notification.id())
                    .maxAttempts(properties.maxAttempts())
                    .errorMessage(getErrorMessage(exception))
                    .build()
            );
        }
    }

    private String getErrorMessage(@Nonnull Exception exception) {
        String message = exception.getMessage();

        if (message == null || message.isBlank()) {
            return exception.getClass().getSimpleName();
        }

        return message.length() > properties.maxErrorMessageLength()
                ? message.substring(0, properties.maxErrorMessageLength())
                : message;
    }

}
