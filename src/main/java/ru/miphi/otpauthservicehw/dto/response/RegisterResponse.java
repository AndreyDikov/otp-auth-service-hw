package ru.miphi.otpauthservicehw.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import ru.miphi.otpauthservicehw.enums.UserRole;

@Builder
public record RegisterResponse(

        @JsonProperty(value = "id", required = true)
        Long id,

        @JsonProperty(value = "login", required = true)
        String login,

        @JsonProperty(value = "role", required = true)
        UserRole role

) {}
