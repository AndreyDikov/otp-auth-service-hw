package ru.miphi.otpauthservicehw.entity.request;

import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record MarkNotificationOutboxFailedEntityRequest(

        @Nonnull
        Long id,

        @Nonnull
        Integer maxAttempts,

        @Nonnull
        String errorMessage

) {}
