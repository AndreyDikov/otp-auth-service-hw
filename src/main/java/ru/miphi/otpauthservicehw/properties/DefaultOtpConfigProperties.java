package ru.miphi.otpauthservicehw.properties;

import jakarta.annotation.Nonnull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.otp.default-config")
public record DefaultOtpConfigProperties(

        @Nonnull
        Integer codeLength,

        @Nonnull
        Integer ttlSeconds

) {}
