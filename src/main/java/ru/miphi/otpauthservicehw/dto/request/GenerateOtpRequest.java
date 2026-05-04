package ru.miphi.otpauthservicehw.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.miphi.otpauthservicehw.enums.NotificationChannel;

public record GenerateOtpRequest(
        @NotBlank
        String operationId,

        @NotNull
        NotificationChannel channel,

        /**
         * EMAIL: email адрес
         * SMS: номер телефона
         * TELEGRAM: можно игнорировать, если chatId один в конфиге
         * FILE: имя пользователя/любой идентификатор
         */
        @NotBlank
        String destination
) {
}
