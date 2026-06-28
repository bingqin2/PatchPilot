CREATE TABLE fix_task_evidence_acceptance_closeout_archive (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    status VARCHAR(32) NOT NULL,
    accepted BOOLEAN NOT NULL,
    summary VARCHAR(1024) NOT NULL,
    latest_archive_id VARCHAR(36),
    latest_task_id VARCHAR(64),
    latest_pull_request_url VARCHAR(512),
    latest_delivery_receipt_id VARCHAR(36),
    latest_delivery_target VARCHAR(512),
    latest_delivery_channel VARCHAR(128),
    delivery_receipt_freshness VARCHAR(32) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    report MEDIUMTEXT NOT NULL,
    INDEX idx_fix_task_evidence_closeout_created (created_at),
    INDEX idx_fix_task_evidence_closeout_latest_archive (latest_archive_id, created_at),
    INDEX idx_fix_task_evidence_closeout_latest_task (latest_task_id, created_at)
);
