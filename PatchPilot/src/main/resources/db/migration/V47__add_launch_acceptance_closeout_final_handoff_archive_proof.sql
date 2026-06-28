ALTER TABLE demo_launch_acceptance_closeout_archive
    ADD COLUMN final_handoff_report_package_archive_status VARCHAR(32) NOT NULL DEFAULT 'NEEDS_ATTENTION',
    ADD COLUMN final_handoff_report_package_archive_ready BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN final_handoff_report_package_archive_id VARCHAR(128),
    ADD COLUMN final_handoff_report_package_archive_summary VARCHAR(1024) NOT NULL DEFAULT 'No final handoff report package archive evidence recorded.';
