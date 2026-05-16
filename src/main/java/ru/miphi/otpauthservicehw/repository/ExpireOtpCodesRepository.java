package ru.miphi.otpauthservicehw.repository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.enums.QueryPath.EXPIRE_OTP_CODES;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ExpireOtpCodesRepository {

    JdbcClient jdbcClient;

    public int expireOtpCodes() {
        return jdbcClient.sql(EXPIRE_OTP_CODES.sql())
                .update();
    }

}
