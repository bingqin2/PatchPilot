CREATE TABLE external_exposure_session (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    status VARCHAR(32) NOT NULL,
    public_url VARCHAR(2048) NOT NULL,
    webhook_url VARCHAR(2048) NOT NULL,
    purpose VARCHAR(1024) NOT NULL,
    operator VARCHAR(255) NOT NULL,
    expected_shutdown_at TIMESTAMP(6) NULL,
    notes TEXT NULL,
    linked_handoff_status VARCHAR(32) NOT NULL,
    linked_readiness_archive_id VARCHAR(36) NULL,
    started_at TIMESTAMP(6) NOT NULL,
    closed_by VARCHAR(255) NULL,
    closed_at TIMESTAMP(6) NULL,
    close_notes TEXT NULL,
    report MEDIUMTEXT NOT NULL,
    INDEX idx_external_exposure_session_started (started_at),
    INDEX idx_external_exposure_session_status_started (status, started_at)
);
