package ru.miphi.otpauthservicehw.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.miphi.otpauthservicehw.dto.request.LoginRequest;
import ru.miphi.otpauthservicehw.dto.response.LoginResponse;
import ru.miphi.otpauthservicehw.entity.response.LoginEntityResponse;
import ru.miphi.otpauthservicehw.exception.BusinessLogicException;
import ru.miphi.otpauthservicehw.repository.LoginRepository;
import ru.miphi.otpauthservicehw.security.JwtProvider;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.exception.ErrorType.INVALID_LOGIN_OR_PASSWORD;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LoginService {

    LoginRepository loginRepository;

    PasswordEncoder passwordEncoder;
    JwtProvider jwtProvider;

    public LoginResponse login(@Nonnull LoginRequest request) {
        LoginEntityResponse user = loginRepository.getUserByLogin(request.login())
                .orElseThrow(() -> BusinessLogicException.of(INVALID_LOGIN_OR_PASSWORD));

        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            throw BusinessLogicException.of(INVALID_LOGIN_OR_PASSWORD);
        }

        return LoginResponse.builder()
                .token(jwtProvider.generateToken(
                        user.id(),
                        user.login(),
                        user.role()
                ))
                .build();
    }

}
