CREATE TABLE demo_launch_evidence_share_delivery_receipt (
    id varchar(36) not null primary key,
    status varchar(32) not null,
    launch_evidence_archive_id varchar(36) not null,
    session_id varchar(128) not null,
    delivery_channel varchar(64) not null,
    delivery_target varchar(512) not null,
    operator varchar(128) not null,
    notes varchar(1024) not null,
    message_subject varchar(512) not null,
    delivered_at timestamp(6) not null,
    created_at timestamp(6) not null,
    markdown_report mediumtext not null,
    INDEX idx_demo_launch_evidence_share_delivery_receipt_created (created_at),
    INDEX idx_demo_launch_evidence_share_delivery_receipt_archive_created (launch_evidence_archive_id, created_at)
);
