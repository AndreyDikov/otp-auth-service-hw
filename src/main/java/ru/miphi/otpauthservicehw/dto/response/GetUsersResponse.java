package ru.miphi.otpauthservicehw.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import ru.miphi.otpauthservicehw.enums.UserRole;

import java.util.List;

@Builder
public record GetUsersResponse(

        @JsonProperty(value = "page", required = true)
        Integer page,

        @JsonProperty(value = "size", required = true)
        Integer size,

        @JsonProperty(value = "total_elements", required = true)
        Long totalElements,

        @JsonProperty(value = "total_pages", required = true)
        Integer totalPages,

        @JsonProperty(value = "users", required = true)
        List<UserResponse> users

) {

        @Builder
        public record UserResponse(

                @JsonProperty(value = "id", required = true)
                Long id,

                @JsonProperty(value = "login", required = true)
                String login,

                @JsonProperty(value = "role", required = true)
                UserRole role

        ) {}

}
