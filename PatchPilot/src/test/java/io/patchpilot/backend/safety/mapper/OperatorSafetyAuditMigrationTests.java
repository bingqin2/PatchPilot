package io.patchpilot.backend.safety.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class OperatorSafetyAuditMigrationTests {

    @Test
    void should_create_operator_safety_audit_table() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V24__create_operator_safety_audit.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("create table operator_safety_audit");
        assertThat(sql).contains("id varchar(36) not null primary key");
        assertThat(sql).contains("action varchar(64) not null");
        assertThat(sql).contains("resource_type varchar(64) not null");
        assertThat(sql).contains("resource_id varchar(128) not null");
        assertThat(sql).contains("scope varchar(32) not null");
        assertThat(sql).contains("scope_key varchar(256) not null");
        assertThat(sql).contains("operator varchar(128) not null");
        assertThat(sql).contains("reason varchar(512) not null");
        assertThat(sql).contains("created_at timestamp(6) not null");
        assertThat(sql).contains("idx_operator_safety_audit_created");
        assertThat(sql).contains("idx_operator_safety_audit_resource");
    }
}
