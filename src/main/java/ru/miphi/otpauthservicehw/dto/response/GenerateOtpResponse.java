package ru.miphi.otpauthservicehw.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record GenerateOtpResponse(

        @JsonProperty(value = "operation_id", required = true)
        String operationId

) {}
