package ru.miphi.otpauthservicehw.service;

import ru.miphi.otpauthservicehw.enums.NotificationChannel;

public interface NotificationService {

    boolean supports(NotificationChannel channel);

    void sendCode(String destination, String code);
}
