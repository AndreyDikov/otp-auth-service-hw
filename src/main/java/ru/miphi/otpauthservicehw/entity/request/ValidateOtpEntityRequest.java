package ru.miphi.otpauthservicehw.entity.request;

import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record ValidateOtpEntityRequest(

        @Nonnull
        Long userId,

        @Nonnull
        String operationId

) {}
