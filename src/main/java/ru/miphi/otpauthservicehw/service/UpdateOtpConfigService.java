package ru.miphi.otpauthservicehw.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.miphi.otpauthservicehw.dto.request.UpdateOtpConfigRequest;
import ru.miphi.otpauthservicehw.dto.response.OtpConfigResponse;
import ru.miphi.otpauthservicehw.entity.request.UpdateOtpConfigEntityRequest;
import ru.miphi.otpauthservicehw.entity.response.UpdateOtpConfigEntityResponse;
import ru.miphi.otpauthservicehw.exception.BusinessLogicException;
import ru.miphi.otpauthservicehw.repository.UpdateOtpConfigRepository;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.exception.ErrorType.OTP_CONFIG_NOT_FOUND;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UpdateOtpConfigService {

    UpdateOtpConfigRepository updateOtpConfigRepository;

    public OtpConfigResponse updateOtpConfig(@Nonnull UpdateOtpConfigRequest request) {
        UpdateOtpConfigEntityRequest entityRequest = UpdateOtpConfigEntityRequest.builder()
                .codeLength(request.codeLength())
                .ttlSeconds(request.ttlSeconds())
                .build();

        UpdateOtpConfigEntityResponse entityResponse = updateOtpConfigRepository.updateOtpConfig(entityRequest)
                .orElseThrow(() -> BusinessLogicException.of(OTP_CONFIG_NOT_FOUND));

        return OtpConfigResponse.builder()
                .codeLength(entityResponse.codeLength())
                .ttlSeconds(entityResponse.ttlSeconds())
                .build();
    }

}
