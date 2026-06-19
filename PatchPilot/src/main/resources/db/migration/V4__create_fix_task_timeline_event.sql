CREATE TABLE fix_task_timeline_event (
    id varchar(36) not null primary key,
    task_id varchar(36) not null,
    event_type varchar(64) not null,
    message varchar(2048) null,
    created_at timestamp(6) not null,
    INDEX idx_fix_task_timeline_event_task_created (task_id, created_at)
);
