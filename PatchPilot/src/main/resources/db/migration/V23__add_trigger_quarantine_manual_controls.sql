ALTER TABLE trigger_quarantine
    ADD COLUMN created_by varchar(128) null,
    ADD COLUMN released_at timestamp(6) null,
    ADD COLUMN released_by varchar(128) null,
    ADD COLUMN release_reason varchar(512) null,
    ADD INDEX idx_trigger_quarantine_released_at (released_at);
