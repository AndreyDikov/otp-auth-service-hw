package ru.miphi.otpauthservicehw.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.miphi.otpauthservicehw.client.resolver.NotificationClientResolver;
import ru.miphi.otpauthservicehw.dto.request.GenerateOtpRequest;
import ru.miphi.otpauthservicehw.dto.response.GenerateOtpResponse;
import ru.miphi.otpauthservicehw.entity.request.CreateOtpCodeEntityRequest;
import ru.miphi.otpauthservicehw.entity.response.GenerateOtpConfigEntityResponse;
import ru.miphi.otpauthservicehw.exception.BusinessLogicException;
import ru.miphi.otpauthservicehw.repository.GenerateOtpRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.exception.ErrorType.OTP_CONFIG_NOT_FOUND;
import static ru.miphi.otpauthservicehw.exception.ErrorType.OTP_HASHING_FAILED;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GenerateOtpService {

    private static final int DECIMAL_RADIX = 10;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    GenerateOtpRepository generateOtpRepository;

    PasswordEncoder passwordEncoder;
    NotificationClientResolver notificationClientResolver;

    public GenerateOtpResponse generateOtp(Long userId, @Nonnull GenerateOtpRequest request) {
        GenerateOtpConfigEntityResponse otpConfig = generateOtpRepository.getOtpConfig()
                .orElseThrow(() -> BusinessLogicException.of(OTP_CONFIG_NOT_FOUND));

        String code = generateCode(otpConfig.codeLength());
        String operationId = request.operationId();

        generateOtpRepository.createOtpCode(CreateOtpCodeEntityRequest.builder()
                .userId(userId)
                .operationId(operationId)
                .codeHash(generateCodeHash(code))
                .expiresAt(LocalDateTime.now().plusSeconds(otpConfig.ttlSeconds()))
                .build()
        );

        //todo: нужен outbox
        notificationClientResolver.resolve(request.notificationChannel())
                .sendCode(request.destination(), code);

        return GenerateOtpResponse.builder()
                .operationId(operationId)
                .build();
    }

    @Nonnull
    private String generateCode(Integer codeLength) {
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < codeLength; i++) {
            code.append(SECURE_RANDOM.nextInt(DECIMAL_RADIX));
        }

        return code.toString();
    }

    @Nonnull
    private String generateCodeHash(String code) {
        String codeHash = passwordEncoder.encode(code);

        if (codeHash == null) {
            throw BusinessLogicException.of(OTP_HASHING_FAILED);
        }

        return codeHash;
    }

}
