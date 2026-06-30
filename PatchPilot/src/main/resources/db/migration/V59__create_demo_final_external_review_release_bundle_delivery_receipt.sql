CREATE TABLE IF NOT EXISTS demo_final_external_review_release_bundle_delivery_receipt (
    id VARCHAR(64) PRIMARY KEY,
    status VARCHAR(32) NOT NULL,
    release_bundle_archive_status VARCHAR(32) NOT NULL,
    release_bundle_archive_id VARCHAR(64) NOT NULL,
    latest_certificate_archive_id VARCHAR(64),
    latest_delivery_finalization_archive_id VARCHAR(64),
    latest_package_archive_id VARCHAR(64),
    latest_package_delivery_receipt_id VARCHAR(64),
    latest_task_id VARCHAR(64),
    latest_pull_request_url VARCHAR(1024),
    summary VARCHAR(1024) NOT NULL,
    next_action VARCHAR(1024) NOT NULL,
    delivery_channel VARCHAR(128) NOT NULL,
    delivery_target VARCHAR(512) NOT NULL,
    operator VARCHAR(128) NOT NULL,
    notes TEXT NOT NULL,
    delivered_at TIMESTAMP(6) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    markdown_report MEDIUMTEXT NOT NULL
);

CREATE INDEX idx_final_external_review_release_bundle_delivery_receipt_created_at
    ON demo_final_external_review_release_bundle_delivery_receipt (created_at);

CREATE INDEX idx_final_external_review_release_bundle_delivery_receipt_archive_id
    ON demo_final_external_review_release_bundle_delivery_receipt (release_bundle_archive_id);

CREATE INDEX idx_final_external_review_release_bundle_delivery_receipt_status
    ON demo_final_external_review_release_bundle_delivery_receipt (status);
