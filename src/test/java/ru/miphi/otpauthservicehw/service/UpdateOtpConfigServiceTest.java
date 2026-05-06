package ru.miphi.otpauthservicehw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.miphi.otpauthservicehw.dto.request.UpdateOtpConfigRequest;
import ru.miphi.otpauthservicehw.dto.response.OtpConfigResponse;
import ru.miphi.otpauthservicehw.entity.request.UpdateOtpConfigEntityRequest;
import ru.miphi.otpauthservicehw.entity.response.UpdateOtpConfigEntityResponse;
import ru.miphi.otpauthservicehw.exception.BusinessLogicException;
import ru.miphi.otpauthservicehw.repository.UpdateOtpConfigRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.miphi.otpauthservicehw.exception.ErrorType.OTP_CONFIG_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class UpdateOtpConfigServiceTest {

    private static final Short CONFIG_ID = 1;
    private static final Integer CODE_LENGTH = 6;
    private static final Integer TTL_SECONDS = 300;
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2026, 5, 5, 10, 0);

    @Mock
    UpdateOtpConfigRepository updateOtpConfigRepository;

    @InjectMocks
    UpdateOtpConfigService updateOtpConfigService;

    @Nested
    class UpdateOtpConfigTest {

        @Test
        @DisplayName("если конфигурация otp-кодов не найдена, метод выбрасывает бизнес-исключение")
        void otpConfigNotFound_shouldThrowBusinessLogicException() {
            when(updateOtpConfigRepository.updateOtpConfig(org.mockito.ArgumentMatchers.any()))
                    .thenReturn(Optional.empty());

            BusinessLogicException exception = assertThrows(
                    BusinessLogicException.class,
                    () -> updateOtpConfigService.updateOtpConfig(buildRequest())
            );

            assertEquals(OTP_CONFIG_NOT_FOUND, exception.getErrorType());

            ArgumentCaptor<UpdateOtpConfigEntityRequest> captor =
                    ArgumentCaptor.forClass(UpdateOtpConfigEntityRequest.class);

            verify(updateOtpConfigRepository).updateOtpConfig(captor.capture());

            UpdateOtpConfigEntityRequest entityRequest = captor.getValue();

            assertEquals(CODE_LENGTH, entityRequest.codeLength());
            assertEquals(TTL_SECONDS, entityRequest.ttlSeconds());
        }

        @Test
        @DisplayName("если конфигурация otp-кодов найдена, метод обновляет конфигурацию")
        void otpConfigFound_shouldUpdateOtpConfig() {
            when(updateOtpConfigRepository.updateOtpConfig(org.mockito.ArgumentMatchers.any()))
                    .thenReturn(Optional.of(buildEntityResponse()));

            OtpConfigResponse response = updateOtpConfigService.updateOtpConfig(buildRequest());

            assertEquals(CODE_LENGTH, response.codeLength());
            assertEquals(TTL_SECONDS, response.ttlSeconds());

            ArgumentCaptor<UpdateOtpConfigEntityRequest> captor =
                    ArgumentCaptor.forClass(UpdateOtpConfigEntityRequest.class);

            verify(updateOtpConfigRepository).updateOtpConfig(captor.capture());

            UpdateOtpConfigEntityRequest entityRequest = captor.getValue();

            assertEquals(CODE_LENGTH, entityRequest.codeLength());
            assertEquals(TTL_SECONDS, entityRequest.ttlSeconds());
        }

    }

    private static UpdateOtpConfigRequest buildRequest() {
        return UpdateOtpConfigRequest.builder()
                .codeLength(CODE_LENGTH)
                .ttlSeconds(TTL_SECONDS)
                .build();
    }

    private static UpdateOtpConfigEntityResponse buildEntityResponse() {
        return UpdateOtpConfigEntityResponse.builder()
                .id(CONFIG_ID)
                .codeLength(CODE_LENGTH)
                .ttlSeconds(TTL_SECONDS)
                .updatedAt(UPDATED_AT)
                .build();
    }

}
