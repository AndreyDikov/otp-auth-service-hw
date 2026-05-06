package ru.miphi.otpauthservicehw.entity.response;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import ru.miphi.otpauthservicehw.enums.NotificationChannel;

@Builder
public record NotificationOutboxEntityResponse(

        @Nonnull
        Long id,

        @Nonnull
        NotificationChannel notificationChannel,

        @Nonnull
        String destination,

        @Nonnull
        String encryptedCode,

        @Nonnull
        Integer attempts

) {}
