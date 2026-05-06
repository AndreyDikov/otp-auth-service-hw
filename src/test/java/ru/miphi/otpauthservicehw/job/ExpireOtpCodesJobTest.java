package ru.miphi.otpauthservicehw.job;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.miphi.otpauthservicehw.repository.ExpireOtpCodesRepository;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpireOtpCodesJobTest {

    @Mock
    ExpireOtpCodesRepository expireOtpCodesRepository;

    @InjectMocks
    ExpireOtpCodesJob expireOtpCodesJob;

    @Nested
    class ExpireOtpCodesTest {

        @Test
        @DisplayName("если устаревшие otp-коды есть, метод обновляет их статус")
        void expiredOtpCodesExist_shouldExpireOtpCodes() {
            when(expireOtpCodesRepository.expireOtpCodes())
                    .thenReturn(3);

            expireOtpCodesJob.expireOtpCodes();

            verify(expireOtpCodesRepository).expireOtpCodes();
        }

        @Test
        @DisplayName("если устаревших otp-кодов нет, метод ничего дополнительно не делает")
        void expiredOtpCodesNotFound_shouldDoNothing() {
            when(expireOtpCodesRepository.expireOtpCodes())
                    .thenReturn(0);

            expireOtpCodesJob.expireOtpCodes();

            verify(expireOtpCodesRepository).expireOtpCodes();
        }

    }

}
