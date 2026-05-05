package ru.miphi.otpauthservicehw.entity.request;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import ru.miphi.otpauthservicehw.enums.UserRole;

@Builder
public record RegisterEntityRequest(

        @Nonnull
        String login,

        @Nonnull
        String passwordHash,

        @Nonnull
        UserRole role

) {}
