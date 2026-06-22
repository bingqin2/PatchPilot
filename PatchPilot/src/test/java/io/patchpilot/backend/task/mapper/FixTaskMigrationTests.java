package io.patchpilot.backend.task.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskMigrationTests {

    @Test
    void should_create_fix_task_table_with_required_columns_and_delivery_unique_key() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V1__create_fix_task.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("create table");
        assertThat(sql).contains("fix_task");
        assertThat(sql).contains("id varchar(36) primary key");
        assertThat(sql).contains("repository_owner varchar(255) not null");
        assertThat(sql).contains("repository_name varchar(255) not null");
        assertThat(sql).contains("issue_number bigint not null");
        assertThat(sql).contains("installation_id bigint not null");
        assertThat(sql).contains("trigger_user varchar(255) not null");
        assertThat(sql).contains("trigger_comment text not null");
        assertThat(sql).contains("delivery_id varchar(255) not null");
        assertThat(sql).contains("comment_id bigint not null");
        assertThat(sql).contains("status varchar(64) not null");
        assertThat(sql).contains("failure_reason text null");
        assertThat(sql).contains("created_at timestamp(6) not null");
        assertThat(sql).contains("unique key uk_fix_task_delivery_id");
        assertThat(sql).contains("(delivery_id)");
    }

    @Test
    void should_add_active_task_issue_lookup_index() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V5__add_fix_task_active_lookup_index.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("alter table fix_task");
        assertThat(sql).contains("idx_fix_task_issue_status");
        assertThat(sql).contains("repository_owner");
        assertThat(sql).contains("repository_name");
        assertThat(sql).contains("issue_number");
        assertThat(sql).contains("status");
    }

    @Test
    void should_add_review_approval_audit_columns() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V15__add_fix_task_review_approval_audit.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("alter table fix_task");
        assertThat(sql).contains("risk_review_approved_by varchar(255) null");
        assertThat(sql).contains("risk_review_approval_reason text null");
    }
}
