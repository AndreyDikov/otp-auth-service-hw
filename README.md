# 🚀 Otp-auth-service-hw

> Сервис для регистрации пользователей, аутентификации и подтверждения операций с помощью OTP-кодов

---

## 📌 Описание

Сервис представляет собой backend-приложение для регистрации и аутентификации пользователей, управления настройками
OTP-кодов, генерации и проверки одноразовых кодов подтверждения операций, а также их отправки через файл, email, SMS и
Telegram

---

## 📘 Документация по процессам

- ### [Endpoints](docs/endpoints/index.md)
- ### [Jobs](docs/jobs/index.md)
- ### [Database](docs/database/index.md)

---

## 🛠️ Стек технологий

- ☕ Java 21 (язык разработки)
- 🌱 Spring boot (framework для разработки REST сервисов)
- 🐘 PostgreSQL (база данных)
- 🗄️ JdbcClient (API поверх JDBC для выполнения SQL-запросов)

---

## 🚀 Локальный запуск

1. Узнать значения чувствительных данных для формирования локального файла-конфига

   | Параметр              | Где взять                                                                                                      |
   |-----------------------|----------------------------------------------------------------------------------------------------------------|
   | `<db_url>`            | JDBC URL PostgreSQL, например `jdbc:postgresql://localhost:5432/otp_service`                                   |
   | `<db_username>`       | Имя пользователя PostgreSQL                                                                                    |
   | `<db_password>`       | Пароль пользователя PostgreSQL                                                                                 |
   | `<jwt_secret>`        | Сектрет для подписи JWT, минимум 32 байта, можно сгенерировать [здесь](https://jwtsecretkeygenerator.com/)     |
   | `<otp_crypto_secret>` | Base64-секрет для шифрования OTP-кодов, можно сгенерировать [здесь](https://generate-random.org/base64-string) |
   | `<email>`             | Почтовый ящик, с которого будут отправляться OTP-коды                                                          |
   | `<email_password>`    | Пароль приложения для SMTP в настройках почтового сервиса                                                      |
   | `<smtp_host>`         | SMTP host почтового сервиса, например `smtp.gmail.com` или `smtp.yandex.com`                                   |
   | `<smpp_client>`       | `system_id` из конфигурации SMPP-эмулятора                                                                     |
   | `<smpp_password>`     | Пароль SMPP-клиента из конфигурации SMPP-эмулятора                                                             |
   | `<tg_token>`          | Токен Telegram-бота от `@BotFather`                                                                            |
   | `<tg_chat_id>`        | ID чата из `getUpdates` после отправки сообщения боту                                                          |

2. Создать файл `application.yaml` в папке `config` в корне проекта (если такой папки нет, то создать) с содержимым ниже
   ```yaml
   spring:
     datasource:
       url: <db_url>
       username: <db_username>
       password: <db_password>
   app:
     jwt:
       secret: <jwt_secret>
     crypto:
       otp-code:
         secret: <otp_crypto_secret>
     notification:
       email:
         username: <email>
         password: <email_password>
         from: <email>
         host: <smtp_host>
     sms:
       system-id: <smpp_client>
       password: <smpp_password>
     telegram:
       bot-token: <tg_token>
       chat-id: <tg_chat_id>
   ```

3. Выполнить команду
   ```shell
   .\gradlew.bat bootRun
   ```

---

## 🔗 Полезные ссылки

| Ссылка                                                 | Примечание                                                      |
|--------------------------------------------------------|-----------------------------------------------------------------|
| [swagger](http://localhost:8080/swagger-ui/index.html) | Открывать только после локального старта приложения (порт 8080) |

---

## 🧪 Тестирование сервиса

Чтобы запустить сразу все Unit-тесты, достаточно выполнить команду
```shell
.\gradlew.bat test
```
