CREATE TABLE fix_task_tool_call (
    id varchar(36) not null primary key,
    task_id varchar(36) not null,
    tool_name varchar(128) not null,
    input_summary varchar(1024) null,
    output_summary text null,
    success boolean not null,
    started_at timestamp(6) not null,
    finished_at timestamp(6) not null,
    duration_ms bigint not null,
    INDEX idx_fix_task_tool_call_task_started (task_id, started_at)
);
