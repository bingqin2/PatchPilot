CREATE TABLE webhook_delivery_diagnostic (
    id varchar(36) not null primary key,
    delivery_id varchar(128) null,
    event varchar(64) not null,
    status varchar(64) not null,
    task_id varchar(36) null,
    repository_owner varchar(128) null,
    repository_name varchar(128) null,
    issue_number bigint null,
    trigger_user varchar(128) null,
    trigger_comment text null,
    message varchar(512) not null,
    created_at timestamp(6) not null,
    INDEX idx_webhook_delivery_diagnostic_created (created_at),
    INDEX idx_webhook_delivery_diagnostic_delivery (delivery_id),
    INDEX idx_webhook_delivery_diagnostic_repository (repository_owner, repository_name, created_at)
);
