package ru.miphi.otpauthservicehw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.miphi.otpauthservicehw.dto.request.ValidateOtpRequest;
import ru.miphi.otpauthservicehw.dto.response.ValidateOtpResponse;
import ru.miphi.otpauthservicehw.entity.request.ValidateOtpEntityRequest;
import ru.miphi.otpauthservicehw.entity.response.ValidateOtpEntityResponse;
import ru.miphi.otpauthservicehw.exception.BusinessLogicException;
import ru.miphi.otpauthservicehw.repository.ValidateOtpRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.miphi.otpauthservicehw.exception.ErrorType.INVALID_OTP_CODE;
import static ru.miphi.otpauthservicehw.exception.ErrorType.OTP_CODE_EXPIRED;
import static ru.miphi.otpauthservicehw.exception.ErrorType.OTP_CODE_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class ValidateOtpServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long OTP_CODE_ID = 10L;

    private static final String OPERATION_ID = "operation-1";
    private static final String CODE = "123456";
    private static final String CODE_HASH = "code-hash";

    @Mock
    ValidateOtpRepository validateOtpRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    ValidateOtpService validateOtpService;

    @Nested
    class ValidateOtpTest {

        @Test
        @DisplayName("если активный otp-код не найден, метод выбрасывает бизнес-исключение")
        void activeOtpCodeNotFound_shouldThrowBusinessLogicException() {
            when(validateOtpRepository.getActiveOtpCode(any()))
                    .thenReturn(Optional.empty());

            BusinessLogicException exception = assertThrows(
                    BusinessLogicException.class,
                    () -> validateOtpService.validateOtp(USER_ID, buildRequest())
            );

            assertEquals(OTP_CODE_NOT_FOUND, exception.getErrorType());

            ArgumentCaptor<ValidateOtpEntityRequest> captor =
                    ArgumentCaptor.forClass(ValidateOtpEntityRequest.class);

            verify(validateOtpRepository).getActiveOtpCode(captor.capture());

            ValidateOtpEntityRequest entityRequest = captor.getValue();

            assertEquals(USER_ID, entityRequest.userId());
            assertEquals(OPERATION_ID, entityRequest.operationId());

            verify(validateOtpRepository, never()).markOtpCodeExpired(OTP_CODE_ID);
            verify(validateOtpRepository, never()).markOtpCodeUsed(OTP_CODE_ID);
            verify(passwordEncoder, never()).matches(CODE, CODE_HASH);
        }

        @Test
        @DisplayName("если otp-код просрочен, метод помечает его просроченным и выбрасывает бизнес-исключение")
        void otpCodeExpired_shouldMarkExpiredAndThrowBusinessLogicException() {
            when(validateOtpRepository.getActiveOtpCode(any()))
                    .thenReturn(Optional.of(buildExpiredOtpCode()));

            BusinessLogicException exception = assertThrows(
                    BusinessLogicException.class,
                    () -> validateOtpService.validateOtp(USER_ID, buildRequest())
            );

            assertEquals(OTP_CODE_EXPIRED, exception.getErrorType());

            verify(validateOtpRepository).markOtpCodeExpired(OTP_CODE_ID);
            verify(validateOtpRepository, never()).markOtpCodeUsed(OTP_CODE_ID);
            verify(passwordEncoder, never()).matches(CODE, CODE_HASH);
        }

        @Test
        @DisplayName("если otp-код неверный, метод выбрасывает бизнес-исключение")
        void otpCodeInvalid_shouldThrowBusinessLogicException() {
            when(validateOtpRepository.getActiveOtpCode(any()))
                    .thenReturn(Optional.of(buildActiveOtpCode()));
            when(passwordEncoder.matches(CODE, CODE_HASH))
                    .thenReturn(false);

            BusinessLogicException exception = assertThrows(
                    BusinessLogicException.class,
                    () -> validateOtpService.validateOtp(USER_ID, buildRequest())
            );

            assertEquals(INVALID_OTP_CODE, exception.getErrorType());

            verify(passwordEncoder).matches(CODE, CODE_HASH);
            verify(validateOtpRepository, never()).markOtpCodeExpired(OTP_CODE_ID);
            verify(validateOtpRepository, never()).markOtpCodeUsed(OTP_CODE_ID);
        }

        @Test
        @DisplayName("если otp-код уже был использован другим запросом, метод выбрасывает бизнес-исключение")
        void otpCodeAlreadyUsedByConcurrentRequest_shouldThrowBusinessLogicException() {
            when(validateOtpRepository.getActiveOtpCode(any()))
                    .thenReturn(Optional.of(buildActiveOtpCode()));
            when(passwordEncoder.matches(CODE, CODE_HASH))
                    .thenReturn(true);
            when(validateOtpRepository.markOtpCodeUsed(OTP_CODE_ID))
                    .thenReturn(0);

            BusinessLogicException exception = assertThrows(
                    BusinessLogicException.class,
                    () -> validateOtpService.validateOtp(USER_ID, buildRequest())
            );

            assertEquals(INVALID_OTP_CODE, exception.getErrorType());

            verify(passwordEncoder).matches(CODE, CODE_HASH);
            verify(validateOtpRepository).markOtpCodeUsed(OTP_CODE_ID);
            verify(validateOtpRepository, never()).markOtpCodeExpired(OTP_CODE_ID);
        }

        @Test
        @DisplayName("если otp-код корректный, метод помечает код использованным и возвращает успешный ответ")
        void otpCodeValid_shouldMarkUsedAndReturnSuccessResponse() {
            when(validateOtpRepository.getActiveOtpCode(any()))
                    .thenReturn(Optional.of(buildActiveOtpCode()));
            when(passwordEncoder.matches(CODE, CODE_HASH))
                    .thenReturn(true);
            when(validateOtpRepository.markOtpCodeUsed(OTP_CODE_ID))
                    .thenReturn(1);

            ValidateOtpResponse response = validateOtpService.validateOtp(USER_ID, buildRequest());

            assertEquals(OPERATION_ID, response.operationId());
            assertEquals(true, response.valid());

            verify(passwordEncoder).matches(CODE, CODE_HASH);
            verify(validateOtpRepository).markOtpCodeUsed(OTP_CODE_ID);
            verify(validateOtpRepository, never()).markOtpCodeExpired(OTP_CODE_ID);
        }

    }

    private static ValidateOtpRequest buildRequest() {
        return ValidateOtpRequest.builder()
                .operationId(OPERATION_ID)
                .code(CODE)
                .build();
    }

    private static ValidateOtpEntityResponse buildActiveOtpCode() {
        return ValidateOtpEntityResponse.builder()
                .id(OTP_CODE_ID)
                .codeHash(CODE_HASH)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
    }

    private static ValidateOtpEntityResponse buildExpiredOtpCode() {
        return ValidateOtpEntityResponse.builder()
                .id(OTP_CODE_ID)
                .codeHash(CODE_HASH)
                .expiresAt(LocalDateTime.now().minusMinutes(5))
                .build();
    }

}
