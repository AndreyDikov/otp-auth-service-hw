package ru.miphi.otpauthservicehw.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ValidateOtpRequest(
        @NotBlank
        String operationId,

        @NotBlank
        String code
) {
}
