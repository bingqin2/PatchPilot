CREATE TABLE fix_task (
    id varchar(36) primary key,
    repository_owner varchar(255) not null,
    repository_name varchar(255) not null,
    issue_number bigint not null,
    installation_id bigint not null,
    trigger_user varchar(255) not null,
    trigger_comment text not null,
    delivery_id varchar(255) not null,
    comment_id bigint not null,
    status varchar(64) not null,
    failure_reason text null,
    created_at timestamp(6) not null,
    unique key uk_fix_task_delivery_id (delivery_id)
);
