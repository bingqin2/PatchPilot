ALTER TABLE fix_task
    ADD COLUMN pull_request_url varchar(2048) null,
    ADD COLUMN completed_at timestamp(6) null,
    ADD COLUMN updated_at timestamp(6) null;
