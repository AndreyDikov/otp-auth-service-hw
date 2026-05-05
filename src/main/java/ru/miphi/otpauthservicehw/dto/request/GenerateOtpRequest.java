package ru.miphi.otpauthservicehw.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.miphi.otpauthservicehw.enums.NotificationChannel;

@Builder
public record GenerateOtpRequest(

        @JsonProperty(value = "operation_id", required = true)
        @NotBlank(message = "идентификатор операции обязателен для заполнения")
        @Size(min = 1, max = 255, message = "идентификатор операции должен содержать от {min} до {max} символов")
        String operationId,

        @JsonProperty(value = "notification_channel", required = true)
        @NotNull(message = "канал отправки otp-кода обязателен для заполнения")
        NotificationChannel notificationChannel,

        @JsonProperty(value = "destination", required = true)
        @NotBlank(message = "получатель otp-кода обязателен для заполнения")
        @Size(min = 1, max = 255, message = "получатель otp-кода должен содержать от {min} до {max} символов")
        String destination

) {}
