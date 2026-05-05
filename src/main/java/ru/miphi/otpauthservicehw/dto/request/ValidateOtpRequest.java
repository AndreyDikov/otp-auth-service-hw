package ru.miphi.otpauthservicehw.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ValidateOtpRequest(

        @JsonProperty(value = "operation_id", required = true)
        @NotBlank(message = "идентификатор операции обязателен для заполнения")
        @Size(min = 1, max = 255, message = "идентификатор операции должен содержать от {min} до {max} символов")
        String operationId,

        @JsonProperty(value = "code", required = true)
        @NotBlank(message = "otp-код обязателен для заполнения")
        @Size(min = 4, max = 10, message = "otp-код должен содержать от {min} до {max} символов")
        String code

) {}
