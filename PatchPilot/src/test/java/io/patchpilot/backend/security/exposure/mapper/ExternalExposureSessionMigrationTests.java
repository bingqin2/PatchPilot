package io.patchpilot.backend.security.exposure.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalExposureSessionMigrationTests {

    @Test
    void should_create_external_exposure_session_table() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V64__create_external_exposure_session.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("create table external_exposure_session");
        assertThat(sql).contains("id varchar(36) not null primary key");
        assertThat(sql).contains("status varchar(32) not null");
        assertThat(sql).contains("public_url varchar(2048) not null");
        assertThat(sql).contains("webhook_url varchar(2048) not null");
        assertThat(sql).contains("purpose varchar(1024) not null");
        assertThat(sql).contains("operator varchar(255) not null");
        assertThat(sql).contains("expected_shutdown_at timestamp(6) null");
        assertThat(sql).contains("linked_handoff_status varchar(32) not null");
        assertThat(sql).contains("linked_readiness_archive_id varchar(36) null");
        assertThat(sql).contains("started_at timestamp(6) not null");
        assertThat(sql).contains("closed_by varchar(255) null");
        assertThat(sql).contains("closed_at timestamp(6) null");
        assertThat(sql).contains("report mediumtext not null");
        assertThat(sql).contains("idx_external_exposure_session_started");
        assertThat(sql).contains("(started_at)");
        assertThat(sql).contains("idx_external_exposure_session_status_started");
        assertThat(sql).contains("(status, started_at)");
    }
}
