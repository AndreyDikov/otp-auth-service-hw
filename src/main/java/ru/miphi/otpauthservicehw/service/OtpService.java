package ru.miphi.otpauthservicehw.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.miphi.otpauthservicehw.dto.request.GenerateOtpRequest;
import ru.miphi.otpauthservicehw.dto.request.ValidateOtpRequest;
import ru.miphi.otpauthservicehw.dto.response.GenerateOtpResponse;
import ru.miphi.otpauthservicehw.exception.BadRequestException;
import ru.miphi.otpauthservicehw.repository.OtpCodeRepository;
import ru.miphi.otpauthservicehw.repository.OtpConfigRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OtpService {

    private final OtpConfigRepository otpConfigRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final List<NotificationService> notificationServices;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final SecureRandom secureRandom = new SecureRandom();

    public OtpService(
            OtpConfigRepository otpConfigRepository,
            OtpCodeRepository otpCodeRepository,
            List<NotificationService> notificationServices
    ) {
        this.otpConfigRepository = otpConfigRepository;
        this.otpCodeRepository = otpCodeRepository;
        this.notificationServices = notificationServices;
    }

    public GenerateOtpResponse generate(Long userId, GenerateOtpRequest request) {
        var config = otpConfigRepository.getConfig();

        String code = generateNumericCode(config.codeLength());
        String codeHash = encoder.encode(code);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(config.ttlSeconds());

        otpCodeRepository.save(
                userId,
                request.operationId(),
                codeHash,
                expiresAt
        );

        NotificationService notificationService = notificationServices.stream()
                .filter(service -> service.supports(request.channel()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Unsupported notification channel"));

        notificationService.sendCode(request.destination(), code);

        return new GenerateOtpResponse(request.operationId(), "OTP_CREATED");
    }

    public void validate(Long userId, ValidateOtpRequest request) {
        var otp = otpCodeRepository.findActiveByUserIdAndOperationId(userId, request.operationId())
                .orElseThrow(() -> new BadRequestException("Active OTP code not found"));

        if (otp.expiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP code expired");
        }

        if (!encoder.matches(request.code(), otp.codeHash())) {
            throw new BadRequestException("Invalid OTP code");
        }

        otpCodeRepository.markUsed(otp.id());
    }

    private String generateNumericCode(int length) {
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;

        return String.valueOf(secureRandom.nextInt(max - min + 1) + min);
    }
}
