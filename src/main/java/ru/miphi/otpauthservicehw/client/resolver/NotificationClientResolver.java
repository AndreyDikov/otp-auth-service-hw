package ru.miphi.otpauthservicehw.client.resolver;

import jakarta.annotation.Nonnull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.miphi.otpauthservicehw.client.NotificationClient;
import ru.miphi.otpauthservicehw.enums.NotificationChannel;
import ru.miphi.otpauthservicehw.exception.BusinessLogicException;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;
import static ru.miphi.otpauthservicehw.exception.ErrorType.UNSUPPORTED_NOTIFICATION_CHANNEL;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class NotificationClientResolver {

    Map<NotificationChannel, NotificationClient> clients;

    public NotificationClientResolver(List<NotificationClient> clients) {
        this.clients = createClientsByChannel(clients);
    }

    public NotificationClient resolve( NotificationChannel channel) {
        NotificationClient client = clients.get(channel);

        if (client == null) {
            throw BusinessLogicException.of(UNSUPPORTED_NOTIFICATION_CHANNEL);
        }

        return client;
    }

    @Nonnull
    private static Map<NotificationChannel, NotificationClient> createClientsByChannel(
            @Nonnull List<NotificationClient> clients
    ) {
        Map<NotificationChannel, NotificationClient> result = new EnumMap<>(NotificationChannel.class);

        for (NotificationClient client : clients) {
            NotificationClient previousClient = result.put(client.channel(), client);

            if (previousClient != null) {
                throw new IllegalStateException("найдено несколько клиентов для канала отправки: " + client.channel());
            }
        }

        return result;
    }

}
