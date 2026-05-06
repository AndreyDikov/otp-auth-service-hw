package ru.miphi.otpauthservicehw.security;

import jakarta.annotation.Nonnull;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OtpCodeCryptoProvider {

    private static final String AES = "AES";
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    private static final String PARTS_DELIMITER = ":";
    private static final int GCM_IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    SecretKeySpec secretKeySpec;

    public OtpCodeCryptoProvider(@Value("${app.crypto.otp-code.secret}") String secretKey) {
        byte[] secret = Base64.getDecoder().decode(secretKey);
        this.secretKeySpec = new SecretKeySpec(secret, AES);
    }

    @Nonnull
    public String encrypt(@Nonnull String value) {
        try {
            byte[] iv = generateIv();

            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKeySpec,
                    new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            );

            byte[] encryptedValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(iv)
                    + PARTS_DELIMITER
                    + Base64.getEncoder().encodeToString(encryptedValue);
        } catch (Exception exception) {
            throw new IllegalStateException("не удалось зашифровать otp-код", exception);
        }
    }

    @Nonnull
    public String decrypt(@Nonnull String encryptedValue) {
        try {
            String[] parts = encryptedValue.split(PARTS_DELIMITER);

            if (parts.length != 2) {
                throw new IllegalArgumentException("некорректный формат зашифрованного otp-кода");
            }

            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] cipherText = Base64.getDecoder().decode(parts[1]);

            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKeySpec,
                    new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            );

            byte[] decryptedValue = cipher.doFinal(cipherText);

            return new String(decryptedValue, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new IllegalStateException("не удалось расшифровать otp-код", exception);
        }
    }

    @Nonnull
    private static byte[] generateIv() {
        byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(iv);

        return iv;
    }

}
