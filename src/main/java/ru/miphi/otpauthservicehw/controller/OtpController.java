package ru.miphi.otpauthservicehw.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.miphi.otpauthservicehw.dto.request.GenerateOtpRequest;
import ru.miphi.otpauthservicehw.dto.request.ValidateOtpRequest;
import ru.miphi.otpauthservicehw.dto.response.GenerateOtpResponse;
import ru.miphi.otpauthservicehw.service.OtpService;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/generate")
    public GenerateOtpResponse generate(
            @RequestAttribute("userId") Long userId,
            @RequestBody @Valid GenerateOtpRequest request
    ) {
        return otpService.generate(userId, request);
    }

    @PostMapping("/validate")
    public void validate(
            @RequestAttribute("userId") Long userId,
            @RequestBody @Valid ValidateOtpRequest request
    ) {
        otpService.validate(userId, request);
    }
}
