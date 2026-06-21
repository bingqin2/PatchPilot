CREATE TABLE rejected_trigger_audit (
    id varchar(36) not null primary key,
    source varchar(64) not null,
    delivery_id varchar(128) not null,
    repository_owner varchar(128) null,
    repository_name varchar(128) null,
    issue_number bigint null,
    trigger_user varchar(128) null,
    trigger_comment text null,
    reason varchar(512) not null,
    created_at timestamp(6) not null,
    INDEX idx_rejected_trigger_audit_created (created_at),
    INDEX idx_rejected_trigger_audit_repository (repository_owner, repository_name, created_at)
);
