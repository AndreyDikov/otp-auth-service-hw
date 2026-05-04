package ru.miphi.otpauthservicehw.exception;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class BusinessLogicException extends RuntimeException {

    ErrorType errorType;

    private BusinessLogicException(@Nonnull ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    @Nonnull
    public static BusinessLogicException of(@Nonnull ErrorType errorType) {
        return new BusinessLogicException(errorType);
    }

}
