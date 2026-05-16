package ru.miphi.otpauthservicehw.properties;

import jakarta.annotation.Nonnull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.job.notification-outbox-cleanup")
public record NotificationOutboxCleanupJobProperties(

        @Nonnull
        Integer sentTtlDays,

        @Nonnull
        Integer failedTtlDays

) {}
