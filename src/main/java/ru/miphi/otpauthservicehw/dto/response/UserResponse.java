package ru.miphi.otpauthservicehw.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import ru.miphi.otpauthservicehw.enums.UserRole;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Builder
@JsonInclude(NON_NULL)
public record UserResponse(

        @JsonProperty(value = "id", required = true)
        Long id,

        @JsonProperty(value = "login", required = true)
        String login,

        @JsonProperty(value = "role", required = true)
        UserRole role

) {}
