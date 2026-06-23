CREATE TABLE operator_safety_audit (
    id varchar(36) not null primary key,
    action varchar(64) not null,
    resource_type varchar(64) not null,
    resource_id varchar(128) not null,
    scope varchar(32) not null,
    scope_key varchar(256) not null,
    operator varchar(128) not null,
    reason varchar(512) not null,
    created_at timestamp(6) not null,
    INDEX idx_operator_safety_audit_created (created_at),
    INDEX idx_operator_safety_audit_resource (resource_type, resource_id, created_at)
);
