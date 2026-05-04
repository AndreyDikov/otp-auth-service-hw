package ru.miphi.otpauthservicehw.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.miphi.otpauthservicehw.repository.OtpCodeRepository;

@Service
public class OtpExpirationService {

    private static final Logger log = LoggerFactory.getLogger(OtpExpirationService.class);

    private final OtpCodeRepository otpCodeRepository;

    public OtpExpirationService(OtpCodeRepository otpCodeRepository) {
        this.otpCodeRepository = otpCodeRepository;
    }

    @Scheduled(fixedDelayString = "${app.otp.expire-check-interval-ms}")
    public void expireOldCodes() {
        int expiredCount = otpCodeRepository.expireOldActiveCodes();

        if (expiredCount > 0) {
            log.info("Expired {} OTP codes", expiredCount);
        }
    }
}
