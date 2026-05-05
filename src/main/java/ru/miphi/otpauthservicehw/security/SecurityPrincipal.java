package ru.miphi.otpauthservicehw.security;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import ru.miphi.otpauthservicehw.enums.UserRole;

@Builder
public record SecurityPrincipal(

        @Nonnull
        Long userId,

        @Nonnull
        String login,

        @Nonnull
        UserRole role

) {}
