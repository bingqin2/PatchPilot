ALTER TABLE rejected_trigger_audit
    ADD COLUMN comment_id bigint null,
    ADD COLUMN comment_url varchar(512) null;
