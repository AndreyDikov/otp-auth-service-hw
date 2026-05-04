package ru.miphi.otpauthservicehw.service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.miphi.otpauthservicehw.enums.NotificationChannel;

import java.util.Properties;

@Service
public class EmailNotificationService implements NotificationService {

    private final String username;
    private final String password;
    private final String fromEmail;
    private final Session session;

    public EmailNotificationService(
            @Value("${app.email.username}") String username,
            @Value("${app.email.password}") String password,
            @Value("${app.email.from}") String fromEmail,
            @Value("${app.email.smtp.host}") String host,
            @Value("${app.email.smtp.port}") String port,
            @Value("${app.email.smtp.auth}") String auth,
            @Value("${app.email.smtp.starttls-enable}") String startTlsEnable
    ) {
        this.username = username;
        this.password = password;
        this.fromEmail = fromEmail;

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", startTlsEnable);

        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.EMAIL;
    }

    @Override
    public void sendCode(String destination, String code) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(destination));
            message.setSubject("Your OTP Code");
            message.setText("Your verification code is: " + code);

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
