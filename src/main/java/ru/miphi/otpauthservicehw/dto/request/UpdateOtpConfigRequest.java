package ru.miphi.otpauthservicehw.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateOtpConfigRequest(

        @JsonProperty(value = "code_length", required = true)
        @NotNull(message = "количество символов otp-кода обязательно для заполнения")
        @Min(value = 4, message = "количество символов otp-кода не может быть меньше {value}")
        @Max(value = 10, message = "количество символов otp-кода не может быть больше {value}")
        Integer codeLength,

        @JsonProperty(value = "ttl_seconds", required = true)
        @NotNull(message = "время жизни otp-кода обязательно для заполнения")
        @Min(value = 1, message = "время жизни otp-кода должно быть больше {value}")
        Integer ttlSeconds

) {}
