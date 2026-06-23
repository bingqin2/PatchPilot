package io.patchpilot.backend.safety.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class TriggerQuarantineMigrationTests {

    @Test
    void should_create_trigger_quarantine_table() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V22__create_trigger_quarantine.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("create table trigger_quarantine");
        assertThat(sql).contains("id varchar(36) not null primary key");
        assertThat(sql).contains("scope varchar(32) not null");
        assertThat(sql).contains("scope_key varchar(256) not null");
        assertThat(sql).contains("reason varchar(512) not null");
        assertThat(sql).contains("category varchar(64) not null");
        assertThat(sql).contains("evidence_count int not null");
        assertThat(sql).contains("window_ms bigint not null");
        assertThat(sql).contains("started_at timestamp(6) not null");
        assertThat(sql).contains("expires_at timestamp(6) not null");
        assertThat(sql).contains("uk_trigger_quarantine_scope_key");
        assertThat(sql).contains("idx_trigger_quarantine_expires_at");
    }

    @Test
    void should_add_manual_release_metadata_to_trigger_quarantine_table() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V23__add_trigger_quarantine_manual_controls.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("alter table trigger_quarantine");
        assertThat(sql).contains("add column created_by varchar(128)");
        assertThat(sql).contains("add column released_at timestamp(6)");
        assertThat(sql).contains("add column released_by varchar(128)");
        assertThat(sql).contains("add column release_reason varchar(512)");
        assertThat(sql).contains("idx_trigger_quarantine_released_at");
    }
}
