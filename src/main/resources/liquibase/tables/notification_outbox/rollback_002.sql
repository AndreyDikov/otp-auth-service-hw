update notification_outbox
set status = 'PENDING'
where status = 'PROCESSING';

alter table notification_outbox
    drop constraint if exists chk_notification_outbox_status;

alter table notification_outbox
    add constraint chk_notification_outbox_status
        check (status in ('PENDING', 'SENT', 'FAILED'));
