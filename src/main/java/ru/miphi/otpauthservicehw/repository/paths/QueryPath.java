package ru.miphi.otpauthservicehw.enums;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public enum QueryPath {

    UPDATE_OTP_CONFIG("queries/update_otp_config.sql"),
    ;

    @Nonnull
    String path;

    @Nonnull
    String sql;

    QueryPath(@Nonnull String path) {
        this.path = path;
        this.sql = readSql(path);
    }

    public String sql() {
        return sql;
    }

    @Nonnull
    private static String readSql(@Nonnull String path) {
        ClassPathResource resource = new ClassPathResource(path);

        try {
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("не удалось прочитать sql файл: " + path, exception);
        }
    }

}
