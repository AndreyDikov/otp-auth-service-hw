package ru.miphi.otpauthservicehw.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.miphi.otpauthservicehw.enums.UserRole;

public record RegisterRequest(
        @NotBlank
        @Size(min = 3, max = 100)
        String login,

        @NotBlank
        @Size(min = 6, max = 100)
        String password,

        @NotNull
        UserRole role
) {
}
