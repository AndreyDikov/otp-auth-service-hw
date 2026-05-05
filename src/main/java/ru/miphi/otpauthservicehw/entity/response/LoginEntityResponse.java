package ru.miphi.otpauthservicehw.entity.response;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import ru.miphi.otpauthservicehw.enums.UserRole;

@Builder
public record LoginEntityResponse(

        @Nonnull
        Long id,

        @Nonnull
        String login,

        @Nonnull
        String passwordHash,

        @Nonnull
        UserRole role

) {}
