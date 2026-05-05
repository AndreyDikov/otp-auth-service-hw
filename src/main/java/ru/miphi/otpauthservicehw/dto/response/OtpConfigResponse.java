package ru.miphi.otpauthservicehw.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record OtpConfigResponse(

        @JsonProperty(value = "code_length", required = true)
        Integer codeLength,

        @JsonProperty(value = "ttl_seconds", required = true)
        Integer ttlSeconds

) {}
