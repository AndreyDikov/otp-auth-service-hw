package ru.miphi.otpauthservicehw.entity.request;

import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record UpdateOtpConfigEntityRequest(

        @Nonnull
        Integer codeLength,

        @Nonnull
        Integer ttlSeconds

) {}
