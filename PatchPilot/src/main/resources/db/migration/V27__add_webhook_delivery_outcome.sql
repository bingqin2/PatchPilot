ALTER TABLE webhook_delivery_diagnostic
    ADD COLUMN outcome_type varchar(64) null,
    ADD COLUMN outcome_id varchar(128) null,
    ADD COLUMN outcome_url varchar(512) null,
    ADD INDEX idx_webhook_delivery_diagnostic_outcome (outcome_type, outcome_id);
