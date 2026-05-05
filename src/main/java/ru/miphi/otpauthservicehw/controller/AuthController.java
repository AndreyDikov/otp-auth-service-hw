package ru.miphi.otpauthservicehw.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.miphi.otpauthservicehw.dto.request.LoginRequest;
import ru.miphi.otpauthservicehw.dto.request.RegisterRequest;
import ru.miphi.otpauthservicehw.dto.response.LoginResponse;
import ru.miphi.otpauthservicehw.dto.response.RegisterResponse;
import ru.miphi.otpauthservicehw.service.LoginService;
import ru.miphi.otpauthservicehw.service.RegisterService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AuthController {

    public static final String BASE = "/auth";
    public static final String REGISTER = BASE + "/register";
    public static final String LOGIN = BASE + "/login";

    RegisterService registerService;
    LoginService loginService;

    @PostMapping(REGISTER)
    @Operation(summary = "регистрация пользователя")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(registerService.register(request));
    }

    @PostMapping(LOGIN)
    @Operation(summary = "аутентификация пользователя")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(loginService.login(request));
    }

}
