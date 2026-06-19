ALTER TABLE fix_task
    ADD INDEX idx_fix_task_issue_status (repository_owner, repository_name, issue_number, status);
