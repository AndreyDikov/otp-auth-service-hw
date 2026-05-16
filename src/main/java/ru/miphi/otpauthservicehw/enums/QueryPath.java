package ru.miphi.otpauthservicehw.enums;

import jakarta.annotation.Nonnull;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
public enum QueryPath {

    INIT_DEFAULT_OTP_CONFIG("queries/init_data/init_default_otp_config.sql"),

    UPDATE_OTP_CONFIG("queries/update_otp_config/update_otp_config.sql"),

    GET_USERS("queries/get_users/get_users.sql"),
    COUNT_USERS("queries/get_users/count_users.sql"),

    GET_USER_ROLE_BY_ID("queries/delete_user/get_user_role_by_id.sql"),
    DELETE_USER("queries/delete_user/delete_user.sql"),

    EXISTS_USER_BY_LOGIN("queries/register/exists_user_by_login.sql"),
    EXISTS_ADMIN("queries/register/exists_admin.sql"),
    REGISTER_USER("queries/register/register_user.sql"),

    GET_USER_BY_LOGIN("queries/get_user_by_login/get_user_by_login.sql"),

    GET_OTP_CONFIG("queries/generate_otp/get_otp_config.sql"),
    CREATE_OTP_CODE("queries/generate_otp/create_otp_code.sql"),
    CREATE_NOTIFICATION_OUTBOX("queries/generate_otp/create_notification_outbox.sql"),

    GET_ACTIVE_OTP_CODE("queries/validate_otp/get_active_otp_code.sql"),
    MARK_OTP_CODE_USED("queries/validate_otp/mark_otp_code_used.sql"),
    MARK_OTP_CODE_EXPIRED("queries/validate_otp/mark_otp_code_expired.sql"),

    EXPIRE_OTP_CODES("queries/expire_otp_codes/expire_otp_codes.sql"),

    GET_PENDING_NOTIFICATION_OUTBOX("queries/notification_outbox/get_pending_notification_outbox.sql"),
    MARK_NOTIFICATION_OUTBOX_SENT("queries/notification_outbox/mark_notification_outbox_sent.sql"),
    MARK_NOTIFICATION_OUTBOX_FAILED("queries/notification_outbox/mark_notification_outbox_failed.sql"),
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
