package ru.miphi.otpauthservicehw.exception;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.miphi.otpauthservicehw.dto.response.ErrorResponse;

import static ru.miphi.otpauthservicehw.exception.ErrorType.INTERNAL_ERROR;
import static ru.miphi.otpauthservicehw.exception.ErrorType.VALIDATION_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(@Nonnull BusinessLogicException exception) {
        ErrorType errorType = exception.getErrorType();

        return buildResponse(
                errorType,
                errorType.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            @Nonnull MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(VALIDATION_ERROR.getMessage());

        return buildResponse(
                VALIDATION_ERROR,
                message
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.error("unexpected error", exception);

        return buildResponse(
                INTERNAL_ERROR,
                INTERNAL_ERROR.getMessage()
        );
    }

    @Nonnull
    private ResponseEntity<ErrorResponse> buildResponse(
            @Nonnull ErrorType errorType,
            String message
    ) {
        return ResponseEntity
                .status(errorType.getStatus())
                .body(ErrorResponse.builder()
                        .errorCode(errorType.getCode())
                        .message(message)
                        .build()
                );
    }

}
