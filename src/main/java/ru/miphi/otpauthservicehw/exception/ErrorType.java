package ru.miphi.otpauthservicehw.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public enum ErrorType {

    USER_NOT_FOUND(1001, NOT_FOUND, "пользователь не найден"),

    OTP_CONFIG_NOT_FOUND(1002, NOT_FOUND, "конфигурация otp-кодов не найдена"),

    VALIDATION_ERROR(9001, BAD_REQUEST, "ошибка валидации запроса"),

    INTERNAL_ERROR(9999, INTERNAL_SERVER_ERROR, "внутренняя ошибка сервера"),

    ADMIN_DELETE_FORBIDDEN(1003, BAD_REQUEST, "нельзя удалить администратора"),
    ;

    Integer code;
    HttpStatus status;
    String message;

}
