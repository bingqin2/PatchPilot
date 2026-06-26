CREATE TABLE evaluation_run_snapshot_archive (
    id varchar(36) not null primary key,
    preview_run_id varchar(128) not null,
    title varchar(255) not null,
    status varchar(32) not null,
    case_count int not null,
    supported_fix_case_count int not null,
    safety_rejection_case_count int not null,
    covered_languages varchar(512) not null,
    covered_build_systems varchar(512) not null,
    expected_verification_commands text not null,
    safety_rejection_categories varchar(512) not null,
    created_at timestamp(6) not null,
    side_effect_contract varchar(1024) not null,
    report mediumtext not null,
    INDEX idx_eval_snapshot_archive_created (created_at),
    INDEX idx_eval_snapshot_archive_preview_created (preview_run_id, created_at)
);
