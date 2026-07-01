CREATE TABLE external_exposure_readiness_archive (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    status VARCHAR(32) NOT NULL,
    safe_to_expose BOOLEAN NOT NULL,
    summary VARCHAR(1024) NOT NULL,
    ready_count INT NOT NULL,
    needs_attention_count INT NOT NULL,
    blocked_count INT NOT NULL,
    total_count INT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    report MEDIUMTEXT NOT NULL,
    INDEX idx_external_exposure_readiness_archive_created (created_at),
    INDEX idx_external_exposure_readiness_archive_status_created (status, created_at)
);
