ALTER TABLE fix_task
    ADD COLUMN status_comment_id bigint null,
    ADD COLUMN status_comment_url varchar(2048) null;
