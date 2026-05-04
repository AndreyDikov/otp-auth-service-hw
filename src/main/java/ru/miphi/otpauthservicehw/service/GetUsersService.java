package ru.miphi.otpauthservicehw.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.miphi.otpauthservicehw.dto.response.UserResponse;
import ru.miphi.otpauthservicehw.repository.GetUsersRepository;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GetUsersService {

    GetUsersRepository getUsersRepository;

    public List<UserResponse> getUsers() {
        return getUsersRepository.getUsers()
                .stream()
                .map(user -> UserResponse.builder()
                        .id(user.id())
                        .login(user.login())
                        .role(user.role())
                        .build()
                )
                .toList();
    }

}
