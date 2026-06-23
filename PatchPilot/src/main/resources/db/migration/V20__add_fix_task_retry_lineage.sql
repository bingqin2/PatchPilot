ALTER TABLE fix_task
    ADD COLUMN retry_source_task_id varchar(36) null,
    ADD COLUMN retry_source_status varchar(64) null,
    ADD COLUMN retry_source_failure_reason text null,
    ADD COLUMN retried_at timestamp(6) null;
