package ru.miphi.otpauthservicehw.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.miphi.otpauthservicehw.dto.request.ValidateOtpRequest;
import ru.miphi.otpauthservicehw.dto.response.ValidateOtpResponse;
import ru.miphi.otpauthservicehw.entity.request.ValidateOtpEntityRequest;
import ru.miphi.otpauthservicehw.entity.response.ValidateOtpEntityResponse;
import ru.miphi.otpauthservicehw.exception.BusinessLogicException;
import ru.miphi.otpauthservicehw.repository.ValidateOtpRepository;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.exception.ErrorType.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ValidateOtpService {

    ValidateOtpRepository validateOtpRepository;

    PasswordEncoder passwordEncoder;

    @Transactional
    public ValidateOtpResponse validateOtp(Long userId, @Nonnull ValidateOtpRequest request) {
        String operationId = request.operationId();

        ValidateOtpEntityResponse otpCode = validateOtpRepository.getActiveOtpCode(ValidateOtpEntityRequest.builder()
                        .userId(userId)
                        .operationId(operationId)
                        .build()
                )
                .orElseThrow(() -> BusinessLogicException.of(OTP_CODE_NOT_FOUND));

        validateAndExpireIfNeeded(otpCode, request);

        int updatedRows = validateOtpRepository.markOtpCodeUsed(otpCode.id());
        if (updatedRows == 0) {
            throw BusinessLogicException.of(INVALID_OTP_CODE);
        }

        return ValidateOtpResponse.builder()
                .operationId(operationId)
                .valid(true)
                .build();
    }

    private void validateAndExpireIfNeeded(@Nonnull ValidateOtpEntityResponse otpCode, ValidateOtpRequest request) {
        if (otpCode.expiresAt().isBefore(LocalDateTime.now())) {
            validateOtpRepository.markOtpCodeExpired(otpCode.id());
            throw BusinessLogicException.of(OTP_CODE_EXPIRED);
        }

        if (!passwordEncoder.matches(request.code(), otpCode.codeHash())) {
            throw BusinessLogicException.of(INVALID_OTP_CODE);
        }
    }

}
