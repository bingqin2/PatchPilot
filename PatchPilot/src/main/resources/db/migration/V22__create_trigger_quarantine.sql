CREATE TABLE trigger_quarantine (
    id varchar(36) not null primary key,
    scope varchar(32) not null,
    scope_key varchar(256) not null,
    reason varchar(512) not null,
    category varchar(64) not null,
    evidence_count int not null,
    window_ms bigint not null,
    started_at timestamp(6) not null,
    expires_at timestamp(6) not null,
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    UNIQUE KEY uk_trigger_quarantine_scope_key (scope, scope_key),
    INDEX idx_trigger_quarantine_expires_at (expires_at),
    INDEX idx_trigger_quarantine_updated_at (updated_at)
);
