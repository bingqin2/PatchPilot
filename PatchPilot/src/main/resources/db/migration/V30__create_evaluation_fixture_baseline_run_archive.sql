CREATE TABLE evaluation_fixture_baseline_run_archive (
    id varchar(36) not null primary key,
    status varchar(32) not null,
    total_case_count int not null,
    executed_case_count int not null,
    passed_case_count int not null,
    failed_case_count int not null,
    skipped_case_count int not null,
    created_at timestamp(6) not null,
    side_effect_contract varchar(1024) not null,
    next_action varchar(1024) not null,
    report mediumtext not null,
    INDEX idx_eval_fixture_baseline_archive_created (created_at),
    INDEX idx_eval_fixture_baseline_archive_status_created (status, created_at)
);
