package ru.miphi.otpauthservicehw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.miphi.otpauthservicehw.dto.response.UserResponse;
import ru.miphi.otpauthservicehw.entity.response.GetUsersEntityResponse;
import ru.miphi.otpauthservicehw.repository.GetUsersRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.miphi.otpauthservicehw.enums.UserRole.USER;

@ExtendWith(MockitoExtension.class)
class GetUsersServiceTest {

    private static final Long FIRST_USER_ID = 1L;
    private static final Long SECOND_USER_ID = 2L;

    private static final String FIRST_LOGIN = "user-1";
    private static final String SECOND_LOGIN = "user-2";

    @Mock
    GetUsersRepository getUsersRepository;

    @InjectMocks
    GetUsersService getUsersService;

    @Nested
    class GetUsersTest {

        @Test
        @DisplayName("если пользователи найдены, метод возвращает список пользователей")
        void usersFound_shouldReturnUsers() {
            when(getUsersRepository.getUsers())
                    .thenReturn(List.of(
                            buildUser(FIRST_USER_ID, FIRST_LOGIN),
                            buildUser(SECOND_USER_ID, SECOND_LOGIN)
                    ));

            List<UserResponse> response = getUsersService.getUsers();

            assertEquals(2, response.size());

            assertEquals(FIRST_USER_ID, response.getFirst().id());
            assertEquals(FIRST_LOGIN, response.getFirst().login());
            assertEquals(USER, response.getFirst().role());

            assertEquals(SECOND_USER_ID, response.get(1).id());
            assertEquals(SECOND_LOGIN, response.get(1).login());
            assertEquals(USER, response.get(1).role());

            verify(getUsersRepository).getUsers();
        }

        @Test
        @DisplayName("если пользователи не найдены, метод возвращает пустой список")
        void usersNotFound_shouldReturnEmptyList() {
            when(getUsersRepository.getUsers())
                    .thenReturn(List.of());

            List<UserResponse> response = getUsersService.getUsers();

            assertEquals(List.of(), response);

            verify(getUsersRepository).getUsers();
        }

    }

    private static GetUsersEntityResponse buildUser(Long id, String login) {
        return GetUsersEntityResponse.builder()
                .id(id)
                .login(login)
                .role(USER)
                .build();
    }

}
