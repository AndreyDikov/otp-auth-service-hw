package ru.miphi.otpauthservicehw.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record GetUsersParamsRequest(

        @Min(value = 0, message = "номер страницы не может быть меньше {value}")
        Integer page,

        @Min(value = 1, message = "размер страницы не может быть меньше {value}")
        @Max(value = 100, message = "размер страницы не может быть больше {value}")
        Integer size

) {}
