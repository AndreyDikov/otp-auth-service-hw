package ru.miphi.otpauthservicehw.entity.response;

import jakarta.annotation.Nonnull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ValidateOtpEntityResponse(

        @Nonnull
        Long id,

        @Nonnull
        String codeHash,

        @Nonnull
        LocalDateTime expiresAt

) {}
