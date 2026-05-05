package ru.miphi.otpauthservicehw.entity.request;

import jakarta.annotation.Nonnull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateOtpCodeEntityRequest(

        @Nonnull
        Long userId,

        @Nonnull
        String operationId,

        @Nonnull
        String codeHash,

        @Nonnull
        LocalDateTime expiresAt

) {}
