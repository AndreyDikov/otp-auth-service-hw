package ru.miphi.otpauthservicehw.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import ru.miphi.otpauthservicehw.properties.DefaultOtpConfigProperties;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.repository.paths.QueryPath.INIT_DEFAULT_OTP_CONFIG;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DefaultOtpConfig {

    JdbcClient jdbcClient;

    DefaultOtpConfigProperties defaultOtpConfigProperties;

    @Bean
    public ApplicationRunner initDefaultOtpConfig() {
        return args -> {
            int insertedRows = jdbcClient.sql(INIT_DEFAULT_OTP_CONFIG.sql())
                    .param("code_length", defaultOtpConfigProperties.codeLength())
                    .param("ttl_seconds", defaultOtpConfigProperties.ttlSeconds())
                    .update();

            if (insertedRows > 0) {
                log.info("default otp config initialized");
            }
        };
    }

}
