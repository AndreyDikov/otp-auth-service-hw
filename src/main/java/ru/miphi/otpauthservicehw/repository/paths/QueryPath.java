package ru.miphi.otpauthservicehw.repository.paths;

import jakarta.annotation.Nonnull;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
public enum QueryPath {

    INIT_DEFAULT_OTP_CONFIG("init_data/init_default_otp_config.sql"),

    UPDATE_OTP_CONFIG("queries/update_otp_config.sql"),
    GET_USERS("queries/get_users.sql"),
    GET_USER_ROLE_BY_ID("queries/get_user_role_by_id.sql"),
    DELETE_USER("queries/delete_user.sql"),
    EXISTS_USER_BY_LOGIN("queries/exists_user_by_login.sql"),
    EXISTS_ADMIN("queries/exists_admin.sql"),
    REGISTER_USER("queries/register_user.sql"),
    GET_USER_BY_LOGIN("queries/get_user_by_login.sql"),
    GET_OTP_CONFIG("queries/get_otp_config.sql"),
    CREATE_OTP_CODE("queries/create_otp_code.sql"),
    GET_ACTIVE_OTP_CODE("queries/get_active_otp_code.sql"),
    MARK_OTP_CODE_USED("queries/mark_otp_code_used.sql"),
    MARK_OTP_CODE_EXPIRED("queries/mark_otp_code_expired.sql"),
    EXPIRE_OTP_CODES("queries/expire_otp_codes.sql"),
    ;

    @Nonnull
    String sql;

    QueryPath(@Nonnull String path) {
        this.sql = readSql(path);
    }

    public String sql() {
        return sql;
    }

    @Nonnull
    private static String readSql(@Nonnull String path) {
        ClassPathResource resource = new ClassPathResource(path);

        try {
            return resource.getContentAsString(UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("не удалось прочитать sql файл: " + path, exception);
        }
    }

}
