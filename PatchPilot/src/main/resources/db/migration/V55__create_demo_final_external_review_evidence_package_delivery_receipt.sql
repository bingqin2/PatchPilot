CREATE TABLE IF NOT EXISTS demo_final_external_review_evidence_package_delivery_receipt (
    id VARCHAR(64) PRIMARY KEY,
    status VARCHAR(32) NOT NULL,
    final_external_review_package_archive_status VARCHAR(32) NOT NULL,
    final_external_review_package_archive_id VARCHAR(64) NOT NULL,
    closeout_archive_id VARCHAR(64),
    completion_archive_id VARCHAR(64),
    completion_evidence_delivery_receipt_id VARCHAR(64),
    latest_task_id VARCHAR(64),
    latest_pull_request_url VARCHAR(1024),
    summary VARCHAR(1024) NOT NULL,
    next_action VARCHAR(1024) NOT NULL,
    delivery_channel VARCHAR(64) NOT NULL,
    delivery_target VARCHAR(512) NOT NULL,
    operator VARCHAR(128) NOT NULL,
    notes VARCHAR(1024) NOT NULL,
    delivered_at TIMESTAMP(6) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    markdown_report MEDIUMTEXT NOT NULL,
    INDEX idx_final_external_review_package_delivery_receipt_created_at (created_at),
    INDEX idx_final_external_review_package_delivery_receipt_archive_created (
        final_external_review_package_archive_id,
        created_at
    )
);
