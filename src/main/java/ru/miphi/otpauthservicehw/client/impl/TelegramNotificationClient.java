package ru.miphi.otpauthservicehw.client.impl;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.miphi.otpauthservicehw.client.NotificationClient;
import ru.miphi.otpauthservicehw.enums.NotificationChannel;
import ru.miphi.otpauthservicehw.properties.TelegramNotificationProperties;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.OK;
import static ru.miphi.otpauthservicehw.enums.NotificationChannel.TELEGRAM;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TelegramNotificationClient implements NotificationClient {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final String SEND_MESSAGE_URL_TEMPLATE = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

    TelegramNotificationProperties properties;

    @Override
    public NotificationChannel channel() {
        return TELEGRAM;
    }

    @Override
    public void sendCode(String destination, String code) {
        HttpRequest request = buildSendMessageRequest(destination, code);
        HttpResponse<String> response = sendRequest(request);

        validateResponse(response);
    }

    private HttpRequest buildSendMessageRequest(String destination, String code) {
        String text = "%s, ваш otp-код: %s".formatted(destination, code);

        String url = SEND_MESSAGE_URL_TEMPLATE.formatted(
                properties.botToken(),
                properties.chatId(),
                URLEncoder.encode(text, UTF_8)
        );

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return HTTP_CLIENT.send(request, BodyHandlers.ofString());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("поток был прерван при отправке otp-кода в telegram", exception);
        } catch (IOException exception) {
            throw new IllegalStateException("не удалось отправить otp-код в telegram", exception);
        }
    }

    private static void validateResponse(@Nonnull HttpResponse<String> response) {
        if (response.statusCode() != OK.value()) {
            throw new IllegalStateException("telegram api вернул статус: " + response.statusCode());
        }
    }

}
