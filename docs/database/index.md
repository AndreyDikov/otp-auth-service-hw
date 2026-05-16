# 🗄️ Database

> База данных используется для хранения пользовательских данных, конфигурации OTP, состояния одноразовых кодов и
> служебных записей, необходимых для асинхронной обработки уведомлений. Управление структурой БД выполняется через
> Liquibase-миграции

## 🧩 Диаграмма базы данных

![db_diagram](../imgs/db_diagram.png)

---

## 📋 Таблицы

- [notification_outbox](notification_outbox_table.md)
- [otp_codes](otp_codes_table.md)
- [otp_config](otp_config_table.md)
- [users](users_table.md)

---

## 🧬 Liquibase

- `changelog-master.yaml` — главный changelog, подключает группы миграций
- `tables/changelog.yaml` — подключает миграции таблиц
- `constraints/changelog.yaml` — подключает миграции ограничений
- У каждой сущности своя папка с отдельным `changelog.yaml`
- Внутри папки сущности лежат пары SQL-файлов: `init_001.sql` / `rollback_001.sql`, `init_002.sql` / `rollback_002.sql`
  и т.д.
- Если схема уже накатана, старые миграции не редактируются — добавляется новая пара `init_N.sql` / `rollback_N.sql`
