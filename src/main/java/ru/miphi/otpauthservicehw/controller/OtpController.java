package ru.miphi.otpauthservicehw.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.miphi.otpauthservicehw.dto.request.GenerateOtpRequest;
import ru.miphi.otpauthservicehw.dto.request.ValidateOtpRequest;
import ru.miphi.otpauthservicehw.dto.response.GenerateOtpResponse;
import ru.miphi.otpauthservicehw.dto.response.ValidateOtpResponse;
import ru.miphi.otpauthservicehw.service.GenerateOtpService;
import ru.miphi.otpauthservicehw.service.ValidateOtpService;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.security.SecurityAttribute.USER_ID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OtpController {

    public static final String BASE = "/otp";
    public static final String GENERATE = BASE + "/generate";
    public static final String VALIDATE = BASE + "/validate";

    GenerateOtpService generateOtpService;
    ValidateOtpService validateOtpService;

    @PostMapping(GENERATE)
    @Operation(summary = "генерация otp-кода")
    public ResponseEntity<GenerateOtpResponse> generateOtp(
            @RequestAttribute(USER_ID) Long userId,
            @RequestBody @Valid GenerateOtpRequest request
    ) {
        return ResponseEntity.ok(generateOtpService.generateOtp(userId, request));
    }

    @PostMapping(VALIDATE)
    @Operation(summary = "валидация otp-кода")
    public ResponseEntity<ValidateOtpResponse> validateOtp(
            @RequestAttribute(USER_ID) Long userId,
            @RequestBody @Valid ValidateOtpRequest request
    ) {
        return ResponseEntity.ok(validateOtpService.validateOtp(userId, request));
    }

}
