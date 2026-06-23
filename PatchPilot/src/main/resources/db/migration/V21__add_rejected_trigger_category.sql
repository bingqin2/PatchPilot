ALTER TABLE rejected_trigger_audit
    ADD COLUMN category varchar(64) not null default 'UNKNOWN';

CREATE INDEX idx_rejected_trigger_audit_category_created
    ON rejected_trigger_audit (category, created_at);
