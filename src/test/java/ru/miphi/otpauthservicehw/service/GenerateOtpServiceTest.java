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
import ru.miphi.otpauthservicehw.dto.request.GenerateOtpRequest;
import ru.miphi.otpauthservicehw.dto.response.GenerateOtpResponse;
import ru.miphi.otpauthservicehw.entity.request.CreateNotificationOutboxEntityRequest;
import ru.miphi.otpauthservicehw.entity.request.CreateOtpCodeEntityRequest;
import ru.miphi.otpauthservicehw.entity.response.GenerateOtpConfigEntityResponse;
import ru.miphi.otpauthservicehw.exception.BusinessLogicException;
import ru.miphi.otpauthservicehw.repository.GenerateOtpRepository;
import ru.miphi.otpauthservicehw.security.OtpCodeCryptoProvider;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.miphi.otpauthservicehw.enums.NotificationChannel.EMAIL;
import static ru.miphi.otpauthservicehw.exception.ErrorType.OTP_CONFIG_NOT_FOUND;
import static ru.miphi.otpauthservicehw.exception.ErrorType.OTP_HASHING_FAILED;

@ExtendWith(MockitoExtension.class)
class GenerateOtpServiceTest {

    private static final Long USER_ID = 1L;
    private static final String OPERATION_ID = "operation-1";
    private static final String DESTINATION = "user@example.com";
    private static final Integer CODE_LENGTH = 6;
    private static final Integer TTL_SECONDS = 300;
    private static final String CODE_HASH = "code-hash";
    private static final String ENCRYPTED_CODE = "encrypted-code";

    @Mock
    GenerateOtpRepository generateOtpRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    OtpCodeCryptoProvider otpCodeCryptoProvider;

    @InjectMocks
    GenerateOtpService generateOtpService;

    @Nested
    class GenerateOtpTest {

        @Test
        @DisplayName("если конфигурация otp-кодов не найдена, метод выбрасывает бизнес-исключение")
        void otpConfigNotFound_shouldThrowBusinessLogicException() {
            when(generateOtpRepository.getOtpConfig())
                    .thenReturn(Optional.empty());

            BusinessLogicException exception = assertThrows(
                    BusinessLogicException.class,
                    () -> generateOtpService.generateOtp(USER_ID, buildRequest())
            );

            assertEquals(OTP_CONFIG_NOT_FOUND, exception.getErrorType());

            verify(generateOtpRepository).getOtpConfig();
            verify(generateOtpRepository, never()).createOtpCode(any());
            verify(generateOtpRepository, never()).createNotificationOutbox(any());
        }

        @Test
        @DisplayName("если хеширование otp-кода вернуло null, метод выбрасывает бизнес-исключение")
        void codeHashIsNull_shouldThrowBusinessLogicException() {
            when(generateOtpRepository.getOtpConfig())
                    .thenReturn(Optional.of(buildOtpConfig()));
            when(passwordEncoder.encode(anyString()))
                    .thenReturn(null);

            BusinessLogicException exception = assertThrows(
                    BusinessLogicException.class,
                    () -> generateOtpService.generateOtp(USER_ID, buildRequest())
            );

            assertEquals(OTP_HASHING_FAILED, exception.getErrorType());

            verify(generateOtpRepository).getOtpConfig();
            verify(passwordEncoder).encode(anyString());
            verify(generateOtpRepository, never()).createOtpCode(any());
            verify(generateOtpRepository, never()).createNotificationOutbox(any());
        }

        @Test
        @DisplayName("если запрос корректный, метод создаёт otp-код и событие outbox")
        void validRequest_shouldCreateOtpCodeAndNotificationOutbox() {
            when(generateOtpRepository.getOtpConfig())
                    .thenReturn(Optional.of(buildOtpConfig()));
            when(passwordEncoder.encode(anyString()))
                    .thenReturn(CODE_HASH);
            when(otpCodeCryptoProvider.encrypt(anyString()))
                    .thenReturn(ENCRYPTED_CODE);

            GenerateOtpResponse response = generateOtpService.generateOtp(USER_ID, buildRequest());

            assertEquals(OPERATION_ID, response.operationId());

            ArgumentCaptor<CreateOtpCodeEntityRequest> otpCodeCaptor =
                    ArgumentCaptor.forClass(CreateOtpCodeEntityRequest.class);
            ArgumentCaptor<CreateNotificationOutboxEntityRequest> outboxCaptor =
                    ArgumentCaptor.forClass(CreateNotificationOutboxEntityRequest.class);

            verify(generateOtpRepository).createOtpCode(otpCodeCaptor.capture());
            verify(generateOtpRepository).createNotificationOutbox(outboxCaptor.capture());

            CreateOtpCodeEntityRequest otpCodeRequest = otpCodeCaptor.getValue();

            assertEquals(USER_ID, otpCodeRequest.userId());
            assertEquals(OPERATION_ID, otpCodeRequest.operationId());
            assertEquals(CODE_HASH, otpCodeRequest.codeHash());
            assertInstanceOf(java.time.LocalDateTime.class, otpCodeRequest.expiresAt());

            CreateNotificationOutboxEntityRequest outboxRequest = outboxCaptor.getValue();

            assertEquals(EMAIL, outboxRequest.notificationChannel());
            assertEquals(DESTINATION, outboxRequest.destination());
            assertEquals(ENCRYPTED_CODE, outboxRequest.encryptedCode());

            verify(passwordEncoder).encode(anyString());
            verify(otpCodeCryptoProvider).encrypt(anyString());
        }

    }

    private static GenerateOtpRequest buildRequest() {
        return GenerateOtpRequest.builder()
                .operationId(OPERATION_ID)
                .notificationChannel(EMAIL)
                .destination(DESTINATION)
                .build();
    }

    private static GenerateOtpConfigEntityResponse buildOtpConfig() {
        return GenerateOtpConfigEntityResponse.builder()
                .codeLength(CODE_LENGTH)
                .ttlSeconds(TTL_SECONDS)
                .build();
    }

}
