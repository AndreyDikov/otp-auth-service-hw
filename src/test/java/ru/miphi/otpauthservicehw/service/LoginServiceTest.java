package ru.miphi.otpauthservicehw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.miphi.otpauthservicehw.dto.request.LoginRequest;
import ru.miphi.otpauthservicehw.dto.response.LoginResponse;
import ru.miphi.otpauthservicehw.entity.response.LoginEntityResponse;
import ru.miphi.otpauthservicehw.exception.BusinessLogicException;
import ru.miphi.otpauthservicehw.repository.LoginRepository;
import ru.miphi.otpauthservicehw.security.JwtProvider;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.miphi.otpauthservicehw.enums.UserRole.USER;
import static ru.miphi.otpauthservicehw.exception.ErrorType.INVALID_LOGIN_OR_PASSWORD;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    private static final Long USER_ID = 1L;
    private static final String LOGIN = "user";
    private static final String PASSWORD = "password";
    private static final String PASSWORD_HASH = "password-hash";
    private static final String TOKEN = "jwt-token";

    @Mock
    LoginRepository loginRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtProvider jwtProvider;

    @InjectMocks
    LoginService loginService;

    @Nested
    class LoginTest {

        @Test
        @DisplayName("если пользователь не найден, метод выбрасывает бизнес-исключение")
        void userNotFound_shouldThrowBusinessLogicException() {
            when(loginRepository.getUserByLogin(LOGIN))
                    .thenReturn(Optional.empty());

            BusinessLogicException exception = assertThrows(
                    BusinessLogicException.class,
                    () -> loginService.login(buildRequest())
            );

            assertEquals(INVALID_LOGIN_OR_PASSWORD, exception.getErrorType());

            verify(loginRepository).getUserByLogin(LOGIN);
            verify(passwordEncoder, never()).matches(PASSWORD, PASSWORD_HASH);
            verify(jwtProvider, never()).generateToken(USER_ID, LOGIN, USER);
        }

        @Test
        @DisplayName("если пароль неверный, метод выбрасывает бизнес-исключение")
        void passwordIsInvalid_shouldThrowBusinessLogicException() {
            when(loginRepository.getUserByLogin(LOGIN))
                    .thenReturn(Optional.of(buildUser()));
            when(passwordEncoder.matches(PASSWORD, PASSWORD_HASH))
                    .thenReturn(false);

            BusinessLogicException exception = assertThrows(
                    BusinessLogicException.class,
                    () -> loginService.login(buildRequest())
            );

            assertEquals(INVALID_LOGIN_OR_PASSWORD, exception.getErrorType());

            verify(loginRepository).getUserByLogin(LOGIN);
            verify(passwordEncoder).matches(PASSWORD, PASSWORD_HASH);
            verify(jwtProvider, never()).generateToken(USER_ID, LOGIN, USER);
        }

        @Test
        @DisplayName("если логин и пароль корректны, метод возвращает токен")
        void credentialsAreValid_shouldReturnToken() {
            when(loginRepository.getUserByLogin(LOGIN))
                    .thenReturn(Optional.of(buildUser()));
            when(passwordEncoder.matches(PASSWORD, PASSWORD_HASH))
                    .thenReturn(true);
            when(jwtProvider.generateToken(USER_ID, LOGIN, USER))
                    .thenReturn(TOKEN);

            LoginResponse response = loginService.login(buildRequest());

            assertEquals(TOKEN, response.token());

            verify(loginRepository).getUserByLogin(LOGIN);
            verify(passwordEncoder).matches(PASSWORD, PASSWORD_HASH);
            verify(jwtProvider).generateToken(USER_ID, LOGIN, USER);
        }

    }

    private static LoginRequest buildRequest() {
        return LoginRequest.builder()
                .login(LOGIN)
                .password(PASSWORD)
                .build();
    }

    private static LoginEntityResponse buildUser() {
        return LoginEntityResponse.builder()
                .id(USER_ID)
                .login(LOGIN)
                .passwordHash(PASSWORD_HASH)
                .role(USER)
                .build();
    }

}
