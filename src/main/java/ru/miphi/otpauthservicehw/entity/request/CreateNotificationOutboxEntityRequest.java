package ru.miphi.otpauthservicehw.entity.request;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import ru.miphi.otpauthservicehw.enums.NotificationChannel;

@Builder
public record CreateNotificationOutboxEntityRequest(

        @Nonnull
        NotificationChannel notificationChannel,

        @Nonnull
        String destination,

        @Nonnull
        String encryptedCode

) {}
