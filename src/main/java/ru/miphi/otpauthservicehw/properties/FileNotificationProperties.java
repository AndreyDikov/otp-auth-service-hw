package ru.miphi.otpauthservicehw.properties;

import jakarta.annotation.Nonnull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.notification.file")
public record FileNotificationProperties(

        @Nonnull
        String path

) {}
