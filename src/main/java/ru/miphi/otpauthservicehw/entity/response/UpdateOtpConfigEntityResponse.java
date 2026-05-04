package ru.miphi.otpauthservicehw.entity.response;

import jakarta.annotation.Nonnull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UpdateOtpConfigEntityResponse(

        @Nonnull
        Short id,

        @Nonnull
        Integer codeLength,

        @Nonnull
        Integer ttlSeconds,

        @Nonnull
        LocalDateTime updatedAt

) {}
