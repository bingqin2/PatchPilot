ALTER TABLE fix_task
    ADD COLUMN risk_review_approved_by varchar(255) null,
    ADD COLUMN risk_review_approval_reason text null;
