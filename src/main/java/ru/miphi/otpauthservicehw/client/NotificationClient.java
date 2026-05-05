package ru.miphi.otpauthservicehw.client;

import ru.miphi.otpauthservicehw.enums.NotificationChannel;

public interface NotificationClient {

    NotificationChannel channel();

    void sendCode(String destination, String code);

}
