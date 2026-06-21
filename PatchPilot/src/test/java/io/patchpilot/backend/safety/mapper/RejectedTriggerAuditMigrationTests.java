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
}
