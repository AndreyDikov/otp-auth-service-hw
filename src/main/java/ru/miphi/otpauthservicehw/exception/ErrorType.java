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

    OTP_CONFIG_NOT_FOUND(1001, NOT_FOUND, "конфигурация otp-кодов не найдена"),

    VALIDATION_ERROR(9001, BAD_REQUEST, "ошибка валидации запроса"),

    INTERNAL_ERROR(9999, INTERNAL_SERVER_ERROR, "внутренняя ошибка сервера"),

    ADMIN_DELETE_FORBIDDEN(1002, BAD_REQUEST, "нельзя удалить администратора"),

    USER_ALREADY_EXISTS(1003, BAD_REQUEST, "пользователь с таким логином уже существует"),

    ADMIN_ALREADY_EXISTS(1004, BAD_REQUEST, "администратор уже существует"),

    PASSWORD_HASHING_FAILED(1005, INTERNAL_SERVER_ERROR, "не удалось захешировать пароль"),

    INVALID_LOGIN_OR_PASSWORD(1006, UNAUTHORIZED, "неверный логин или пароль"),

    OTP_HASHING_FAILED(1007, INTERNAL_SERVER_ERROR, "не удалось захешировать otp-код"),

    UNSUPPORTED_NOTIFICATION_CHANNEL(1008, BAD_REQUEST, "неподдерживаемый канал отправки otp-кода"),

    OTP_CODE_NOT_FOUND(1009, NOT_FOUND, "активный otp-код не найден"),

    OTP_CODE_EXPIRED(1010, BAD_REQUEST, "otp-код просрочен"),

    INVALID_OTP_CODE(1011, BAD_REQUEST, "неверный otp-код"),

    INVALID_TOKEN(2001, UNAUTHORIZED, "некорректный или просроченный токен"),

    AUTHORIZATION_HEADER_NOT_FOUND(2002, UNAUTHORIZED, "заголовок авторизации не найден"),

    ACCESS_DENIED(2003, FORBIDDEN, "доступ запрещён"),
    ;

    Integer code;
    HttpStatus status;
    String message;

}
