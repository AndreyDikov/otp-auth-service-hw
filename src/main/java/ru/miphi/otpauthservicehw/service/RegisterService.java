package ru.miphi.otpauthservicehw.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.miphi.otpauthservicehw.dto.request.RegisterRequest;
import ru.miphi.otpauthservicehw.dto.response.RegisterResponse;
import ru.miphi.otpauthservicehw.entity.request.RegisterEntityRequest;
import ru.miphi.otpauthservicehw.entity.response.RegisterEntityResponse;
import ru.miphi.otpauthservicehw.exception.BusinessLogicException;
import ru.miphi.otpauthservicehw.repository.RegisterRepository;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.enums.UserRole.ADMIN;
import static ru.miphi.otpauthservicehw.exception.ErrorType.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RegisterService {

    RegisterRepository registerRepository;

    PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse register(@Nonnull RegisterRequest request) {
        validateRequest(request);

        RegisterEntityRequest entityRequest = mapToEntityRequest(request);

        return mapToDtoResponse(registerRepository.registerUser(entityRequest));
    }

    private void validateRequest(@Nonnull RegisterRequest request) {
        if (registerRepository.existsUserByLogin(request.login())) {
            throw BusinessLogicException.of(USER_ALREADY_EXISTS);
        }

        if (request.role() == ADMIN && registerRepository.existsAdmin()) {
            throw BusinessLogicException.of(ADMIN_ALREADY_EXISTS);
        }
    }

    private RegisterEntityRequest mapToEntityRequest(@Nonnull RegisterRequest request) {
        String passwordHash = passwordEncoder.encode(request.password());

        if (passwordHash == null) {
            throw BusinessLogicException.of(PASSWORD_HASHING_FAILED);
        }

        return RegisterEntityRequest.builder()
                .login(request.login())
                .passwordHash(passwordHash)
                .role(request.role())
                .build();
    }

    private static RegisterResponse mapToDtoResponse(@Nonnull RegisterEntityResponse response) {
        return RegisterResponse.builder()
                .id(response.id())
                .login(response.login())
                .role(response.role())
                .build();
    }

}
