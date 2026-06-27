CREATE TABLE demo_launch_evidence_package_archive (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    status VARCHAR(32) NOT NULL,
    ready_to_share BOOLEAN NOT NULL,
    summary VARCHAR(1024) NOT NULL,
    session_id VARCHAR(128) NOT NULL,
    launch_readiness_status VARCHAR(32) NOT NULL,
    evidence_bundle_status VARCHAR(32) NOT NULL,
    handoff_finalization_status VARCHAR(32) NOT NULL,
    latest_task_id VARCHAR(64),
    latest_pull_request_url VARCHAR(512),
    latest_webhook_delivery_id VARCHAR(128),
    evaluation_run_id VARCHAR(128),
    created_at TIMESTAMP(6) NOT NULL,
    report MEDIUMTEXT NOT NULL,
    INDEX idx_demo_launch_evidence_package_archive_created (created_at),
    INDEX idx_demo_launch_evidence_package_archive_status_created (status, created_at)
);
