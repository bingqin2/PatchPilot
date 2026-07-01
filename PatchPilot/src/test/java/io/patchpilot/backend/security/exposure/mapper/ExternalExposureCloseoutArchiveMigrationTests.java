package io.patchpilot.backend.security.exposure.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalExposureCloseoutArchiveMigrationTests {

    @Test
    void should_create_external_exposure_closeout_archive_table() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V65__create_external_exposure_closeout_archive.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("create table external_exposure_closeout_archive");
        assertThat(sql).contains("id varchar(36) not null primary key");
        assertThat(sql).contains("status varchar(32) not null");
        assertThat(sql).contains("closeout_ready boolean not null");
        assertThat(sql).contains("summary varchar(1024) not null");
        assertThat(sql).contains("next_action varchar(1024) not null");
        assertThat(sql).contains("latest_session_id varchar(36)");
        assertThat(sql).contains("latest_session_status varchar(32)");
        assertThat(sql).contains("public_url varchar(2048)");
        assertThat(sql).contains("webhook_url varchar(2048)");
        assertThat(sql).contains("linked_readiness_archive_id varchar(36)");
        assertThat(sql).contains("handoff_status varchar(32)");
        assertThat(sql).contains("archive_freshness varchar(32)");
        assertThat(sql).contains("ready_count int not null");
        assertThat(sql).contains("needs_attention_count int not null");
        assertThat(sql).contains("blocked_count int not null");
        assertThat(sql).contains("total_count int not null");
        assertThat(sql).contains("generated_at timestamp(6) not null");
        assertThat(sql).contains("archived_at timestamp(6) not null");
        assertThat(sql).contains("evidence_notes text not null");
        assertThat(sql).contains("next_actions text not null");
        assertThat(sql).contains("download_actions text not null");
        assertThat(sql).contains("side_effect_contract varchar(1024) not null");
        assertThat(sql).contains("report mediumtext not null");
        assertThat(sql).contains("idx_external_exposure_closeout_archive_archived");
        assertThat(sql).contains("(archived_at)");
        assertThat(sql).contains("idx_external_exposure_closeout_archive_status_archived");
        assertThat(sql).contains("(status, archived_at)");
        assertThat(sql).contains("idx_external_exposure_closeout_archive_session");
        assertThat(sql).contains("(latest_session_id)");
    }
}
