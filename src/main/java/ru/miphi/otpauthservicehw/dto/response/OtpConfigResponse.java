package ru.miphi.otpauthservicehw.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
public record OtpConfigResponse(

        @JsonProperty(value = "code_length", required = true)
        Integer codeLength,

        @JsonProperty(value = "ttl_seconds", required = true)
        Integer ttlSeconds

) {}
