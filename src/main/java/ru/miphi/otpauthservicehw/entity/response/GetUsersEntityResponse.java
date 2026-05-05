package ru.miphi.otpauthservicehw.entity.response;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import ru.miphi.otpauthservicehw.enums.UserRole;

@Builder
public record GetUsersEntityResponse(

        @Nonnull
        Long id,

        @Nonnull
        String login,

        @Nonnull
        UserRole role

) {}
