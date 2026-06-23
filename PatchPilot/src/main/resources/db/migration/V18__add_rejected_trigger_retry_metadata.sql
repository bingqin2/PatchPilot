ALTER TABLE rejected_trigger_audit
    ADD COLUMN retried_task_id varchar(36) null,
    ADD COLUMN retried_at timestamp(6) null;

CREATE INDEX idx_rejected_trigger_audit_retried_task
    ON rejected_trigger_audit (retried_task_id);
