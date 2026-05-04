package ru.miphi.otpauthservicehw.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
public record ErrorResponse(

        @JsonProperty(value = "error_code", required = true)
        Integer errorCode,

        @JsonProperty(value = "message", required = true)
        String message

) {}
