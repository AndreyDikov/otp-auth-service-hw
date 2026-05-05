package ru.miphi.otpauthservicehw.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.miphi.otpauthservicehw.client.NotificationClient;
import ru.miphi.otpauthservicehw.enums.NotificationChannel;
import ru.miphi.otpauthservicehw.properties.FileNotificationProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.enums.NotificationChannel.FILE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FileNotificationClient implements NotificationClient {

    FileNotificationProperties properties;

    @Override
    public NotificationChannel channel() {
        return FILE;
    }

    @Override
    public void sendCode(
            String destination,
            String code
    ) {
        Path path = Path.of(properties.path());

        String message = "destination=%s, code=%s%n".formatted(destination, code);

        try {
            Files.writeString(path, message, CREATE, APPEND);
        } catch (IOException exception) {
            throw new IllegalStateException("не удалось записать otp-код в файл", exception);
        }
    }

}
