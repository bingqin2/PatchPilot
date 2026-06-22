ALTER TABLE fix_task
    ADD COLUMN language varchar(64) null,
    ADD COLUMN build_system varchar(64) null,
    ADD COLUMN verification_command varchar(512) null;
