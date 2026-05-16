package ru.miphi.otpauthservicehw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.miphi.otpauthservicehw.dto.request.RegisterRequest;
import ru.miphi.otpauthservicehw.dto.response.RegisterResponse;
import ru.miphi.otpauthservicehw.entity.request.RegisterEntityRequest;
import ru.miphi.otpauthservicehw.entity.response.RegisterEntityResponse;
import ru.miphi.otpauthservicehw.exception.BusinessLogicException;
import ru.miphi.otpauthservicehw.repository.RegisterRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.miphi.otpauthservicehw.enums.UserRole.ADMIN;
import static ru.miphi.otpauthservicehw.enums.UserRole.USER;
import static ru.miphi.otpauthservicehw.exception.ErrorType.ADMIN_ALREADY_EXISTS;
import static ru.miphi.otpauthservicehw.exception.ErrorType.PASSWORD_HASHING_FAILED;
import static ru.miphi.otpauthservicehw.exception.ErrorType.USER_ALREADY_EXISTS;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    private static final Long USER_ID = 1L;
    private static final String LOGIN = "user";
    private static final String PASSWORD = "password";
    private static final String PASSWORD_HASH = "password-hash";

    @Mock
    RegisterRepository registerRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    RegisterService registerService;

    @Nested
    class RegisterTest {

        @Test
        @DisplayName("если пользователь с таким логином уже существует, метод выбрасывает бизнес-исключение")
        void userAlreadyExists_shouldThrowBusinessLogicException() {
            when(registerRepository.existsUserByLogin(LOGIN))
                    .thenReturn(true);

            BusinessLogicException exception = assertThrows(
                    BusinessLogicException.class,
                    () -> registerService.register(buildUserRequest())
            );

            assertEquals(USER_ALREADY_EXISTS, exception.getErrorType());

            verify(registerRepository).existsUserByLogin(LOGIN);
            verify(registerRepository, never()).existsAdmin();
            verify(passwordEncoder, never()).encode(PASSWORD);
            verify(registerRepository, never()).registerUser(any());
        }

        @Test
        @DisplayName("если администратор уже существует, метод выбрасывает бизнес-исключение")
        void adminAlreadyExists_shouldThrowBusinessLogicException() {
            when(registerRepository.existsUserByLogin(LOGIN))
                    .thenReturn(false);
            when(registerRepository.existsAdmin())
                    .thenReturn(true);

            BusinessLogicException exception = assertThrows(
                    BusinessLogicException.class,
                    () -> registerService.register(buildAdminRequest())
            );

            assertEquals(ADMIN_ALREADY_EXISTS, exception.getErrorType());

            verify(registerRepository).existsUserByLogin(LOGIN);
            verify(registerRepository).existsAdmin();
            verify(passwordEncoder, never()).encode(PASSWORD);
            verify(registerRepository, never()).registerUser(any());
        }

        @Test
        @DisplayName("если хеширование пароля вернуло null, метод выбрасывает бизнес-исключение")
        void passwordHashIsNull_shouldThrowBusinessLogicException() {
            when(registerRepository.existsUserByLogin(LOGIN))
                    .thenReturn(false);
            when(passwordEncoder.encode(PASSWORD))
                    .thenReturn(null);

            BusinessLogicException exception = assertThrows(
                    BusinessLogicException.class,
                    () -> registerService.register(buildUserRequest())
            );

            assertEquals(PASSWORD_HASHING_FAILED, exception.getErrorType());

            verify(registerRepository).existsUserByLogin(LOGIN);
            verify(registerRepository, never()).existsAdmin();
            verify(passwordEncoder).encode(PASSWORD);
            verify(registerRepository, never()).registerUser(any());
        }

        @Test
        @DisplayName("если регистрируется обычный пользователь, метод создаёт пользователя")
        void regularUserRequest_shouldRegisterUser() {
            when(registerRepository.existsUserByLogin(LOGIN))
                    .thenReturn(false);
            when(passwordEncoder.encode(PASSWORD))
                    .thenReturn(PASSWORD_HASH);
            when(registerRepository.registerUser(any()))
                    .thenReturn(buildUserEntityResponse());

            RegisterResponse response = registerService.register(buildUserRequest());

            assertEquals(USER_ID, response.id());
            assertEquals(LOGIN, response.login());
            assertEquals(USER, response.role());

            ArgumentCaptor<RegisterEntityRequest> captor = ArgumentCaptor.forClass(RegisterEntityRequest.class);

            verify(registerRepository).registerUser(captor.capture());

            RegisterEntityRequest entityRequest = captor.getValue();

            assertEquals(LOGIN, entityRequest.login());
            assertEquals(PASSWORD_HASH, entityRequest.passwordHash());
            assertEquals(USER, entityRequest.role());

            verify(registerRepository).existsUserByLogin(LOGIN);
            verify(registerRepository, never()).existsAdmin();
            verify(passwordEncoder).encode(PASSWORD);
        }

        @Test
        @DisplayName("если регистрируется первый администратор, метод создаёт администратора")
        void firstAdminRequest_shouldRegisterAdmin() {
            when(registerRepository.existsUserByLogin(LOGIN))
                    .thenReturn(false);
            when(registerRepository.existsAdmin())
                    .thenReturn(false);
            when(passwordEncoder.encode(PASSWORD))
                    .thenReturn(PASSWORD_HASH);
            when(registerRepository.registerUser(any()))
                    .thenReturn(buildAdminEntityResponse());

            RegisterResponse response = registerService.register(buildAdminRequest());

            assertEquals(USER_ID, response.id());
            assertEquals(LOGIN, response.login());
            assertEquals(ADMIN, response.role());

            ArgumentCaptor<RegisterEntityRequest> captor = ArgumentCaptor.forClass(RegisterEntityRequest.class);

            verify(registerRepository).registerUser(captor.capture());

            RegisterEntityRequest entityRequest = captor.getValue();

            assertEquals(LOGIN, entityRequest.login());
            assertEquals(PASSWORD_HASH, entityRequest.passwordHash());
            assertEquals(ADMIN, entityRequest.role());

            verify(registerRepository).existsUserByLogin(LOGIN);
            verify(registerRepository).existsAdmin();
            verify(passwordEncoder).encode(PASSWORD);
        }

    }

    private static RegisterRequest buildUserRequest() {
        return RegisterRequest.builder()
                .login(LOGIN)
                .password(PASSWORD)
                .role(USER)
                .build();
    }

    private static RegisterRequest buildAdminRequest() {
        return RegisterRequest.builder()
                .login(LOGIN)
                .password(PASSWORD)
                .role(ADMIN)
                .build();
    }

    private static RegisterEntityResponse buildUserEntityResponse() {
        return RegisterEntityResponse.builder()
                .id(USER_ID)
                .login(LOGIN)
                .role(USER)
                .build();
    }

    private static RegisterEntityResponse buildAdminEntityResponse() {
        return RegisterEntityResponse.builder()
                .id(USER_ID)
                .login(LOGIN)
                .role(ADMIN)
                .build();
    }

}
