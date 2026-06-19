CREATE TABLE fix_task_test_run (
    id varchar(36) not null primary key,
    task_id varchar(36) not null,
    command varchar(1024) not null,
    exit_code int not null,
    output text null,
    started_at timestamp(6) not null,
    finished_at timestamp(6) not null,
    duration_ms bigint not null,
    INDEX idx_fix_task_test_run_task_started (task_id, started_at)
);
