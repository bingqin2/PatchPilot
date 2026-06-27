ALTER TABLE demo_handoff_package_archive
    ADD COLUMN handoff_readiness_status VARCHAR(32) NOT NULL DEFAULT 'NEEDS_ATTENTION',
    ADD COLUMN handoff_readiness_summary VARCHAR(1024) NOT NULL DEFAULT 'No handoff readiness metadata recorded.',
    ADD COLUMN handoff_readiness_next_action VARCHAR(1024) NOT NULL DEFAULT 'Regenerate and archive a fresh handoff package to capture readiness metadata.',
    ADD COLUMN handoff_ready_check_count INT NOT NULL DEFAULT 0,
    ADD COLUMN handoff_needs_attention_check_count INT NOT NULL DEFAULT 0,
    ADD COLUMN handoff_blocked_check_count INT NOT NULL DEFAULT 0;
