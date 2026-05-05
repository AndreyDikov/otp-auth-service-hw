package ru.miphi.otpauthservicehw.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ErrorResponse(

        @JsonProperty(value = "error_code", required = true)
        Integer errorCode,

        @JsonProperty(value = "message", required = true)
        String message

) {}
