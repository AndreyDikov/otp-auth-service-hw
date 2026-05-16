package ru.miphi.otpauthservicehw.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.miphi.otpauthservicehw.dto.request.GetUsersParamsRequest;
import ru.miphi.otpauthservicehw.dto.response.GetUsersResponse;
import ru.miphi.otpauthservicehw.entity.request.GetUsersEntityRequest;
import ru.miphi.otpauthservicehw.repository.GetUsersRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetUsersService {

    private static final int DEFAULT_SIZE = 20;

    GetUsersRepository getUsersRepository;

    @Transactional(readOnly = true)
    public GetUsersResponse getUsers(@Nonnull GetUsersParamsRequest params) {
        int page = params.page();
        int size = params.size() == 0 ? DEFAULT_SIZE : params.size();

        List<GetUsersResponse.UserResponse> users = getUsersRepository.getUsers(GetUsersEntityRequest.builder()
                        .limit(size)
                        .offset(page * size)
                        .build()
                )
                .stream()
                .map(entity -> GetUsersResponse.UserResponse.builder()
                        .id(entity.id())
                        .login(entity.login())
                        .role(entity.role())
                        .build()
                )
                .toList();

        Long totalElements = getUsersRepository.countUsers();

        return GetUsersResponse.builder()
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(Math.toIntExact((totalElements + size - 1) / size))
                .users(users)
                .build();
    }

}
