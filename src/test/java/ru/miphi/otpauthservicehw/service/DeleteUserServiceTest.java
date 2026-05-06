package ru.miphi.otpauthservicehw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.miphi.otpauthservicehw.exception.BusinessLogicException;
import ru.miphi.otpauthservicehw.repository.DeleteUserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.miphi.otpauthservicehw.enums.UserRole.ADMIN;
import static ru.miphi.otpauthservicehw.enums.UserRole.USER;
import static ru.miphi.otpauthservicehw.exception.ErrorType.ADMIN_DELETE_FORBIDDEN;

@ExtendWith(MockitoExtension.class)
class DeleteUserServiceTest {

    private static final Long USER_ID = 1L;

    @Mock
    DeleteUserRepository deleteUserRepository;

    @InjectMocks
    DeleteUserService deleteUserService;

    @Nested
    class DeleteUserTest {

        @Test
        @DisplayName("если пользователь не найден, метод ничего не делает")
        void userNotFound_shouldDoNothing() {
            when(deleteUserRepository.findUserRoleById(USER_ID))
                    .thenReturn(Optional.empty());

            assertDoesNotThrow(() -> deleteUserService.deleteUser(USER_ID));
            verify(deleteUserRepository).findUserRoleById(USER_ID);
            verify(deleteUserRepository, never()).deleteUser(USER_ID);
        }

        @Test
        @DisplayName("если пользователь администратор, метод выбрасывает бизнес-исключение")
        void userIsAdmin_shouldThrowBusinessLogicException() {
            when(deleteUserRepository.findUserRoleById(USER_ID))
                    .thenReturn(Optional.of(ADMIN));

            BusinessLogicException exception = assertThrows(
                    BusinessLogicException.class,
                    () -> deleteUserService.deleteUser(USER_ID)
            );

            assertEquals(ADMIN_DELETE_FORBIDDEN, exception.getErrorType());
            verify(deleteUserRepository).findUserRoleById(USER_ID);
            verify(deleteUserRepository, never()).deleteUser(USER_ID);
        }

        @Test
        @DisplayName("если пользователь обычный, метод удаляет пользователя")
        void userIsRegularUser_shouldDeleteUser() {
            when(deleteUserRepository.findUserRoleById(USER_ID))
                    .thenReturn(Optional.of(USER));

            deleteUserService.deleteUser(USER_ID);

            verify(deleteUserRepository).findUserRoleById(USER_ID);
            verify(deleteUserRepository).deleteUser(USER_ID);
        }

    }

}
