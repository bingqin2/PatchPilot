CREATE TABLE fix_task_queue_item (
    id varchar(36) not null primary key,
    task_id varchar(36) not null,
    status varchar(32) not null,
    attempt_count int not null,
    last_error text null,
    available_at timestamp(6) not null,
    locked_at timestamp(6) null,
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    INDEX idx_fix_task_queue_item_status_available (status, available_at)
);
