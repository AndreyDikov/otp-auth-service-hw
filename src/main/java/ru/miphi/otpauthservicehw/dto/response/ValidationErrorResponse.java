package ru.miphi.otpauthservicehw.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record ValidationErrorResponse(

        @JsonProperty(value = "error_code", required = true)
        Integer errorCode,

        @JsonProperty(value = "message", required = true)
        String message,

        @JsonProperty(value = "field_errors", required = true)
        List<FieldErrorResponse> fieldErrors

) {

    @Builder
    public record FieldErrorResponse(

            @JsonProperty(value = "field", required = true)
            String field,

            @JsonProperty(value = "message", required = true)
            String message

    ) {}

}
