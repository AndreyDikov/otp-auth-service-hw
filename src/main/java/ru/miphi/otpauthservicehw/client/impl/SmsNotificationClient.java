package ru.miphi.otpauthservicehw.client.impl;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.springframework.stereotype.Component;
import ru.miphi.otpauthservicehw.client.NotificationClient;
import ru.miphi.otpauthservicehw.enums.NotificationChannel;
import ru.miphi.otpauthservicehw.properties.SmsNotificationProperties;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.text.DateFormat.DEFAULT;
import static lombok.AccessLevel.PRIVATE;
import static org.jsmpp.bean.Alphabet.ALPHA_DEFAULT;
import static ru.miphi.otpauthservicehw.enums.NotificationChannel.SMS;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class SmsNotificationClient implements NotificationClient {

    private static final byte PROTOCOL_ID = 0;
    private static final byte PRIORITY_FLAG = 1;
    private static final byte REPLACE_IF_PRESENT_FLAG = 0;
    private static final byte DEFAULT_MESSAGE_ID = 0;

    SmsNotificationProperties properties;

    @Override
    public NotificationChannel channel() {
        return SMS;
    }

    @Override
    public void sendCode(String destination, String code) {
        SMPPSession session = new SMPPSession();

        try {
            connectAndBind(session);
            submitMessage(session, destination, code);
        } catch (Exception exception) {
            throw new IllegalStateException("не удалось отправить otp-код по sms", exception);
        } finally {
            closeSession(session);
        }
    }

    private void connectAndBind(@Nonnull SMPPSession session) throws IOException {
        session.connectAndBind(
                properties.host(),
                properties.port(),
                buildBindParameter()
        );
    }

    @Nonnull
    private BindParameter buildBindParameter() {
        return new BindParameter(
                BindType.BIND_TX,
                properties.systemId(),
                properties.password(),
                properties.systemType(),
                TypeOfNumber.UNKNOWN,
                NumberingPlanIndicator.UNKNOWN,
                properties.sourceAddress()
        );
    }

    private void submitMessage(
            @Nonnull SMPPSession session,
            String destination,
            String code
    ) throws PDUException,
            ResponseTimeoutException,
            InvalidResponseException,
            NegativeResponseException,
            IOException {
        session.submitShortMessage(
                properties.systemType(),
                TypeOfNumber.UNKNOWN,
                NumberingPlanIndicator.UNKNOWN,
                properties.sourceAddress(),
                TypeOfNumber.UNKNOWN,
                NumberingPlanIndicator.UNKNOWN,
                destination,
                new ESMClass(),
                PROTOCOL_ID,
                PRIORITY_FLAG,
                null,
                null,
                new RegisteredDelivery(DEFAULT),
                REPLACE_IF_PRESENT_FLAG,
                new GeneralDataCoding(ALPHA_DEFAULT),
                DEFAULT_MESSAGE_ID,
                ("ваш otp-код: " + code).getBytes(UTF_8)
        );
    }

    private void closeSession(SMPPSession session) {
        try {
            session.unbindAndClose();
        } catch (Exception exception) {
            log.warn("не удалось закрыть smpp-сессию", exception);
        }
    }

}
