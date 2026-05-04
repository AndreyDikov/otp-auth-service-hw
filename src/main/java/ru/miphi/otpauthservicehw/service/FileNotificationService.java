package ru.miphi.otpauthservicehw.service;

import org.springframework.stereotype.Service;
import ru.miphi.otpauthservicehw.enums.NotificationChannel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Service
public class FileNotificationService implements NotificationService {

    private static final Path OUTPUT_PATH = Path.of("otp-codes.txt");

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.FILE;
    }

    @Override
    public void sendCode(String destination, String code) {
        String line = "destination=%s, code=%s%n".formatted(destination, code);

        try {
            Files.writeString(
                    OUTPUT_PATH,
                    line,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to write OTP code to file", e);
        }
    }
}
