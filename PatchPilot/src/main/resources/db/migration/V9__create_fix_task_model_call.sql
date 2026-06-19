CREATE TABLE fix_task_model_call (
    id varchar(36) not null primary key,
    task_id varchar(36) not null,
    provider varchar(64) not null,
    model varchar(128) not null,
    prompt_summary varchar(1024) null,
    response_summary text null,
    prompt_tokens int not null,
    completion_tokens int not null,
    total_tokens int not null,
    success boolean not null,
    error_message text null,
    started_at timestamp(6) not null,
    finished_at timestamp(6) not null,
    duration_ms bigint not null,
    INDEX idx_fix_task_model_call_task_started (task_id, started_at)
);
