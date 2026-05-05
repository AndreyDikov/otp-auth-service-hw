package ru.miphi.otpauthservicehw.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.miphi.otpauthservicehw.enums.UserRole;

@Builder
public record RegisterRequest(

        @JsonProperty(value = "login", required = true)
        @NotBlank(message = "логин обязателен для заполнения")
        @Size(min = 3, max = 100, message = "логин должен содержать от {min} до {max} символов")
        String login,

        @JsonProperty(value = "password", required = true)
        @NotBlank(message = "пароль обязателен для заполнения")
        @Size(min = 6, max = 100, message = "пароль должен содержать от {min} до {max} символов")
        String password,

        @JsonProperty(value = "role", required = true)
        @NotNull(message = "роль пользователя обязательна для заполнения")
        UserRole role

) {}
