package ru.miphi.otpauthservicehw.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ValidateOtpResponse(

        @JsonProperty(value = "operation_id", required = true)
        String operationId,

        @JsonProperty(value = "valid", required = true)
        Boolean valid

) {}
