package ru.miphi.otpauthservicehw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.miphi.otpauthservicehw.dto.request.GetUsersParamsRequest;
import ru.miphi.otpauthservicehw.dto.response.GetUsersResponse;
import ru.miphi.otpauthservicehw.entity.request.GetUsersEntityRequest;
import ru.miphi.otpauthservicehw.entity.response.GetUsersEntityResponse;
import ru.miphi.otpauthservicehw.repository.GetUsersRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.miphi.otpauthservicehw.enums.UserRole.USER;

@ExtendWith(MockitoExtension.class)
class GetUsersServiceTest {

    private static final Long FIRST_USER_ID = 1L;
    private static final Long SECOND_USER_ID = 2L;

    private static final String FIRST_LOGIN = "user-1";
    private static final String SECOND_LOGIN = "user-2";

    private static final int DEFAULT_SIZE = 20;
    private static final int PAGE = 0;
    private static final int SIZE = 10;
    private static final int SECOND_PAGE = 2;
    private static final long TOTAL_ELEMENTS = 35L;

    @Mock
    GetUsersRepository getUsersRepository;

    @InjectMocks
    GetUsersService getUsersService;

    @Nested
    class GetUsersTest {

        @Test
        @DisplayName("если пользователи найдены, метод возвращает страницу пользователей")
        void usersFound_shouldReturnUsersPage() {
            when(getUsersRepository.getUsers(any()))
                    .thenReturn(List.of(
                            buildUser(FIRST_USER_ID, FIRST_LOGIN),
                            buildUser(SECOND_USER_ID, SECOND_LOGIN)
                    ));
            when(getUsersRepository.countUsers())
                    .thenReturn(TOTAL_ELEMENTS);

            GetUsersResponse response = getUsersService.getUsers(buildParams(PAGE, SIZE));

            assertEquals(PAGE, response.page());
            assertEquals(SIZE, response.size());
            assertEquals(TOTAL_ELEMENTS, response.totalElements());
            assertEquals(4, response.totalPages());
            assertEquals(2, response.users().size());

            assertEquals(FIRST_USER_ID, response.users().getFirst().id());
            assertEquals(FIRST_LOGIN, response.users().getFirst().login());
            assertEquals(USER, response.users().getFirst().role());

            assertEquals(SECOND_USER_ID, response.users().get(1).id());
            assertEquals(SECOND_LOGIN, response.users().get(1).login());
            assertEquals(USER, response.users().get(1).role());

            ArgumentCaptor<GetUsersEntityRequest> captor = ArgumentCaptor.forClass(GetUsersEntityRequest.class);

            verify(getUsersRepository).getUsers(captor.capture());
            verify(getUsersRepository).countUsers();

            GetUsersEntityRequest entityRequest = captor.getValue();

            assertEquals(SIZE, entityRequest.limit());
            assertEquals(0, entityRequest.offset());
        }

        @Test
        @DisplayName("если размер страницы равен нулю, метод использует размер страницы по умолчанию")
        void sizeIsZero_shouldUseDefaultSize() {
            when(getUsersRepository.getUsers(any()))
                    .thenReturn(List.of());
            when(getUsersRepository.countUsers())
                    .thenReturn(TOTAL_ELEMENTS);

            GetUsersResponse response = getUsersService.getUsers(buildParams(PAGE, 0));

            assertEquals(PAGE, response.page());
            assertEquals(DEFAULT_SIZE, response.size());
            assertEquals(TOTAL_ELEMENTS, response.totalElements());
            assertEquals(2, response.totalPages());
            assertEquals(List.of(), response.users());

            ArgumentCaptor<GetUsersEntityRequest> captor = ArgumentCaptor.forClass(GetUsersEntityRequest.class);

            verify(getUsersRepository).getUsers(captor.capture());
            verify(getUsersRepository).countUsers();

            GetUsersEntityRequest entityRequest = captor.getValue();

            assertEquals(DEFAULT_SIZE, entityRequest.limit());
            assertEquals(0, entityRequest.offset());
        }

        @Test
        @DisplayName("если запрошена не первая страница, метод корректно рассчитывает offset")
        void notFirstPage_shouldCalculateOffset() {
            when(getUsersRepository.getUsers(any()))
                    .thenReturn(List.of());
            when(getUsersRepository.countUsers())
                    .thenReturn(TOTAL_ELEMENTS);

            GetUsersResponse response = getUsersService.getUsers(buildParams(SECOND_PAGE, SIZE));

            assertEquals(SECOND_PAGE, response.page());
            assertEquals(SIZE, response.size());
            assertEquals(TOTAL_ELEMENTS, response.totalElements());
            assertEquals(4, response.totalPages());

            ArgumentCaptor<GetUsersEntityRequest> captor = ArgumentCaptor.forClass(GetUsersEntityRequest.class);

            verify(getUsersRepository).getUsers(captor.capture());
            verify(getUsersRepository).countUsers();

            GetUsersEntityRequest entityRequest = captor.getValue();

            assertEquals(SIZE, entityRequest.limit());
            assertEquals(SECOND_PAGE * SIZE, entityRequest.offset());
        }

        @Test
        @DisplayName("если пользователей нет, метод возвращает пустую страницу")
        void usersNotFound_shouldReturnEmptyPage() {
            when(getUsersRepository.getUsers(any()))
                    .thenReturn(List.of());
            when(getUsersRepository.countUsers())
                    .thenReturn(0L);

            GetUsersResponse response = getUsersService.getUsers(buildParams(PAGE, SIZE));

            assertEquals(PAGE, response.page());
            assertEquals(SIZE, response.size());
            assertEquals(0L, response.totalElements());
            assertEquals(0, response.totalPages());
            assertEquals(List.of(), response.users());

            verify(getUsersRepository).getUsers(any());
            verify(getUsersRepository).countUsers();
        }

    }

    private static GetUsersParamsRequest buildParams(int page, int size) {
        return GetUsersParamsRequest.builder()
                .page(page)
                .size(size)
                .build();
    }

    private static GetUsersEntityResponse buildUser(Long id, String login) {
        return GetUsersEntityResponse.builder()
                .id(id)
                .login(login)
                .role(USER)
                .build();
    }

}