package io.patchpilot.backend.safety.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class RejectedTriggerAuditMigrationTests {

    @Test
    void should_create_rejected_trigger_audit_table() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V11__create_rejected_trigger_audit.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("create table rejected_trigger_audit");
        assertThat(sql).contains("id varchar(36) not null primary key");
        assertThat(sql).contains("source varchar(64) not null");
        assertThat(sql).contains("delivery_id varchar(128) not null");
        assertThat(sql).contains("repository_owner varchar(128) null");
        assertThat(sql).contains("repository_name varchar(128) null");
        assertThat(sql).contains("issue_number bigint null");
        assertThat(sql).contains("trigger_user varchar(128) null");
        assertThat(sql).contains("trigger_comment text null");
        assertThat(sql).contains("reason varchar(512) not null");
        assertThat(sql).contains("created_at timestamp(6) not null");
        assertThat(sql).contains("idx_rejected_trigger_audit_created");
        assertThat(sql).contains("idx_rejected_trigger_audit_repository");
    }

    @Test
    void should_add_refusal_comment_metadata_to_rejected_trigger_audit() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V17__add_rejected_trigger_comment_metadata.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("alter table rejected_trigger_audit");
        assertThat(sql).contains("add column comment_id bigint null");
        assertThat(sql).contains("add column comment_url varchar(512) null");
    }

    @Test
    void should_add_retry_metadata_to_rejected_trigger_audit() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V18__add_rejected_trigger_retry_metadata.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("alter table rejected_trigger_audit");
        assertThat(sql).contains("add column retried_task_id varchar(36) null");
        assertThat(sql).contains("add column retried_at timestamp(6) null");
        assertThat(sql).contains("idx_rejected_trigger_audit_retried_task");
    }

    @Test
    void should_add_rejected_trigger_category() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V21__add_rejected_trigger_category.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("alter table rejected_trigger_audit");
        assertThat(sql).contains("add column category varchar(64) not null default 'unknown'");
        assertThat(sql).contains("idx_rejected_trigger_audit_category_created");
    }
}
