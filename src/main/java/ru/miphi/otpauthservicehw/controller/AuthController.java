package ru.miphi.otpauthservicehw.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.miphi.otpauthservicehw.dto.request.LoginRequest;
import ru.miphi.otpauthservicehw.dto.request.RegisterRequest;
import ru.miphi.otpauthservicehw.dto.response.AuthResponse;
import ru.miphi.otpauthservicehw.dto.response.UserResponse;
import ru.miphi.otpauthservicehw.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public UserResponse register(@RequestBody @Valid RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }
}
