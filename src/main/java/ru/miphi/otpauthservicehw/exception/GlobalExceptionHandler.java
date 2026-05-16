package ru.miphi.otpauthservicehw.exception;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.miphi.otpauthservicehw.dto.response.ErrorResponse;
import ru.miphi.otpauthservicehw.dto.response.ValidationErrorResponse;

import java.util.List;

import static ru.miphi.otpauthservicehw.exception.ErrorType.INTERNAL_ERROR;
import static ru.miphi.otpauthservicehw.exception.ErrorType.VALIDATION_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(@Nonnull BusinessLogicException exception) {
        ErrorType errorType = exception.getErrorType();

        return buildErrorResponse(
                errorType,
                errorType.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(
            @Nonnull MethodArgumentNotValidException exception
    ) {
        List<ValidationErrorResponse.FieldErrorResponse> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(GlobalExceptionHandler::mapToFieldErrorResponse)
                .toList();

        return ResponseEntity.status(VALIDATION_ERROR.getStatus())
                .body(ValidationErrorResponse.builder()
                        .errorCode(VALIDATION_ERROR.getCode())
                        .message(VALIDATION_ERROR.getMessage())
                        .fieldErrors(fieldErrors)
                        .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(@Nonnull Exception exception) {
        log.error("Неожиданная ошибка", exception);

        return buildErrorResponse(
                INTERNAL_ERROR,
                INTERNAL_ERROR.getMessage()
        );
    }

    @Nonnull
    private static ValidationErrorResponse.FieldErrorResponse mapToFieldErrorResponse(
            @Nonnull FieldError fieldError
    ) {
        return ValidationErrorResponse.FieldErrorResponse.builder()
                .field(toSnakeCase(fieldError.getField()))
                .message(fieldError.getDefaultMessage())
                .build();
    }

    @Nonnull
    private ResponseEntity<ErrorResponse> buildErrorResponse(@Nonnull ErrorType errorType, String message) {
        return ResponseEntity.status(errorType.getStatus())
                .body(ErrorResponse.builder()
                        .errorCode(errorType.getCode())
                        .message(message)
                        .build()
                );
    }

    @Nonnull
    private static String toSnakeCase(@Nonnull String value) {
        return value.replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }

}
