package ru.miphi.otpauthservicehw.properties;

import jakarta.annotation.Nonnull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.notification.email")
public record EmailNotificationProperties(

        @Nonnull
        String username,

        @Nonnull
        String password,

        @Nonnull
        String from,

        @Nonnull
        String host,

        @Nonnull
        Integer port,

        @Nonnull
        Boolean auth,

        @Nonnull
        Boolean starttlsEnable

) {}
