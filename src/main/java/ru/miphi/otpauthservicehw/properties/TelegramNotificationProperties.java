package ru.miphi.otpauthservicehw.properties;

import jakarta.annotation.Nonnull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.notification.telegram")
public record TelegramNotificationProperties(

        @Nonnull
        String botToken,

        @Nonnull
        String chatId

) {}
