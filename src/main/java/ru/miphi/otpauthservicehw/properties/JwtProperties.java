package ru.miphi.otpauthservicehw.properties;

import jakarta.annotation.Nonnull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(

        @Nonnull
        String secret,

        @Nonnull
        Long expirationMs

) {}
