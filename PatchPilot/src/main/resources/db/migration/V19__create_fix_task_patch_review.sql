CREATE TABLE fix_task_patch_review (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    task_id VARCHAR(36) NOT NULL,
    decision VARCHAR(32) NOT NULL,
    reason TEXT NOT NULL,
    confidence VARCHAR(32) NOT NULL,
    required_follow_up TEXT NULL,
    edited_files TEXT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    INDEX idx_fix_task_patch_review_task_created (task_id, created_at)
);
