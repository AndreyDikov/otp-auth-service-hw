package ru.miphi.otpauthservicehw.client.impl;

import jakarta.annotation.Nonnull;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.miphi.otpauthservicehw.client.NotificationClient;
import ru.miphi.otpauthservicehw.enums.NotificationChannel;
import ru.miphi.otpauthservicehw.properties.EmailNotificationProperties;

import java.util.Properties;

import static jakarta.mail.Message.RecipientType.TO;
import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.enums.NotificationChannel.EMAIL;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class EmailNotificationClient implements NotificationClient {

    EmailNotificationProperties properties;

    Session session;

    public EmailNotificationClient(EmailNotificationProperties properties) {
        this.properties = properties;
        this.session = createSession(properties);
    }

    @Override
    public NotificationChannel channel() {
        return EMAIL;
    }

    @Override
    public void sendCode(String destination, String code) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(properties.from()));
            message.setRecipient(TO, new InternetAddress(destination));
            message.setSubject("otp-код подтверждения");
            message.setText("ваш otp-код: " + code);

            Transport.send(message);
        } catch (MessagingException exception) {
            throw new IllegalStateException("не удалось отправить otp-код по email", exception);
        }
    }

    @Nonnull
    private static Session createSession(@Nonnull EmailNotificationProperties properties) {
        Properties sessionProperties = new Properties();
        sessionProperties.put("mail.smtp.host", properties.host());
        sessionProperties.put("mail.smtp.port", properties.port());
        sessionProperties.put("mail.smtp.auth", properties.auth());
        sessionProperties.put("mail.smtp.starttls.enable", properties.starttlsEnable());

        return Session.getInstance(sessionProperties, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        properties.username(),
                        properties.password()
                );
            }

        });
    }

}
