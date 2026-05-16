package ru.miphi.otpauthservicehw.entity.request;

import jakarta.annotation.Nonnull;
import lombok.Builder;

@Builder
public record GetUsersEntityRequest(

        @Nonnull
        Integer limit,

        @Nonnull
        Integer offset

) {}
