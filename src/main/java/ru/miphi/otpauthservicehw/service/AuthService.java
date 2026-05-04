package ru.miphi.otpauthservicehw.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.miphi.otpauthservicehw.dto.request.LoginRequest;
import ru.miphi.otpauthservicehw.dto.request.RegisterRequest;
import ru.miphi.otpauthservicehw.dto.response.AuthResponse;
import ru.miphi.otpauthservicehw.dto.response.UserResponse;
import ru.miphi.otpauthservicehw.exception.BadRequestException;
import ru.miphi.otpauthservicehw.exception.UnauthorizedException;
import ru.miphi.otpauthservicehw.enums.UserRole;
import ru.miphi.otpauthservicehw.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordService passwordService,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request) {
        if (request.role() == UserRole.ADMIN && userRepository.existsAdmin()) {
            throw new BadRequestException("Administrator already exists");
        }

        String passwordHash = passwordService.hash(request.password());

        try {
            var user = userRepository.save(request.login(), passwordHash, request.role());
            return new UserResponse(user.id(), user.login(), user.role());
        } catch (DuplicateKeyException e) {
            throw new BadRequestException("User with this login already exists");
        }
    }

    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByLogin(request.login())
                .orElseThrow(() -> new UnauthorizedException("Invalid login or password"));

        if (!passwordService.matches(request.password(), user.passwordHash())) {
            throw new UnauthorizedException("Invalid login or password");
        }

        return new AuthResponse(jwtService.generateToken(user));
    }
}
