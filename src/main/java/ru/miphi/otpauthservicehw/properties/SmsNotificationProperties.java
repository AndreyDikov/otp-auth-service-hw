package ru.miphi.otpauthservicehw.properties;

import jakarta.annotation.Nonnull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.notification.sms")
public record SmsNotificationProperties(

        @Nonnull
        String host,

        @Nonnull
        Integer port,

        @Nonnull
        String systemId,

        @Nonnull
        String password,

        @Nonnull
        String systemType,

        @Nonnull
        String sourceAddress

) {}
