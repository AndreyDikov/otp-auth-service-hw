package ru.miphi.otpauthservicehw.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.miphi.otpauthservicehw.repository.ExpireOtpCodesRepository;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ExpireOtpCodesJob {

    ExpireOtpCodesRepository expireOtpCodesRepository;

    @Scheduled(
            fixedDelayString = "${app.job.otp-expiration.fixed-delay-ms}",
            initialDelayString = "${app.job.otp-expiration.initial-delay-ms}"
    )
    public void expireOtpCodes() {
        int expiredCodesCount = expireOtpCodesRepository.expireOtpCodes();

        if (expiredCodesCount > 0) {
            log.info("Количество устаревших OTP-кодов: {}", expiredCodesCount);
        }
    }

}
