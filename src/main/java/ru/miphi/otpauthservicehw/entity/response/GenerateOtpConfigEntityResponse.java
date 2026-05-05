package ru.miphi.otpauthservicehw.entity.response;

import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record GenerateOtpConfigEntityResponse(

        @Nonnull
        Integer codeLength,

        @Nonnull
        Integer ttlSeconds

) {}
