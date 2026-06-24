CREATE TABLE demo_session_archive (
    id varchar(36) not null primary key,
    session_id varchar(128) not null,
    status varchar(32) not null,
    summary varchar(1024) not null,
    share_summary varchar(1024) not null,
    recent_pull_request_url varchar(512) null,
    created_at timestamp(6) not null,
    report mediumtext not null,
    INDEX idx_demo_session_archive_created (created_at),
    INDEX idx_demo_session_archive_session_created (session_id, created_at)
);
