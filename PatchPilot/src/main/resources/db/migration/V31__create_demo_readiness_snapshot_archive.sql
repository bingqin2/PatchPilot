CREATE TABLE demo_readiness_snapshot_archive (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    status VARCHAR(32) NOT NULL,
    summary VARCHAR(1024) NOT NULL,
    ready_check_count INT NOT NULL,
    needs_attention_check_count INT NOT NULL,
    blocked_check_count INT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    report MEDIUMTEXT NOT NULL,
    INDEX idx_demo_readiness_snapshot_archive_created (created_at),
    INDEX idx_demo_readiness_snapshot_archive_status_created (status, created_at)
);
