package ru.miphi.otpauthservicehw.properties;

import jakarta.annotation.Nonnull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.job.notification-outbox")
public record NotificationOutboxJobProperties(

        @Nonnull
        Integer batchSize,

        @Nonnull
        Integer maxAttempts,

        @Nonnull
        Integer maxErrorMessageLength

) {}
