CREATE TABLE fix_task_evidence_package_archive (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    task_id VARCHAR(64) NOT NULL,
    repository_owner VARCHAR(255) NOT NULL,
    repository_name VARCHAR(255) NOT NULL,
    issue_number BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL,
    pull_request_url VARCHAR(512),
    archived_at TIMESTAMP(6) NOT NULL,
    summary VARCHAR(1024) NOT NULL,
    report MEDIUMTEXT NOT NULL,
    INDEX idx_fix_task_evidence_archive_task_archived (task_id, archived_at),
    INDEX idx_fix_task_evidence_archive_repo_issue_archived (repository_owner, repository_name, issue_number, archived_at)
);
