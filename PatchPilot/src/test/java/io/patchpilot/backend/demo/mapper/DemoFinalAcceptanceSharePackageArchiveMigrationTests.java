package io.patchpilot.backend.demo.mapper;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalAcceptanceSharePackageArchiveMigrationTests {

    @Test
    void creates_final_acceptance_share_package_archive_table() throws Exception {
        String migration = Files.readString(Path.of(
                "src/main/resources/db/migration/V49__create_demo_final_acceptance_share_package_archive.sql"
        ));

        assertThat(migration).contains("CREATE TABLE IF NOT EXISTS demo_final_acceptance_share_package_archive");
        assertThat(migration).contains("id VARCHAR(64) PRIMARY KEY");
        assertThat(migration).contains("status VARCHAR(32) NOT NULL");
        assertThat(migration).contains("send_ready BOOLEAN NOT NULL");
        assertThat(migration).contains("launch_certificate_archive_id VARCHAR(64)");
        assertThat(migration).contains("task_certificate_archive_id VARCHAR(64)");
        assertThat(migration).contains("latest_task_id VARCHAR(64)");
        assertThat(migration).contains("latest_pull_request_url VARCHAR(512)");
        assertThat(migration).contains("recommended_recipients_json JSON NOT NULL");
        assertThat(migration).contains("required_attachments_json JSON NOT NULL");
        assertThat(migration).contains("pre_send_checks_json JSON NOT NULL");
        assertThat(migration).contains("evidence_notes_json JSON NOT NULL");
        assertThat(migration).contains("message_subject VARCHAR(512) NOT NULL");
        assertThat(migration).contains("message_body MEDIUMTEXT NOT NULL");
        assertThat(migration).contains("side_effect_contract VARCHAR(1024) NOT NULL");
        assertThat(migration).contains("report MEDIUMTEXT NOT NULL");
        assertThat(migration).contains("generated_at TIMESTAMP(6) NOT NULL");
        assertThat(migration).contains("archived_at TIMESTAMP(6) NOT NULL");
        assertThat(migration).contains("idx_final_acceptance_share_package_archive_archived_at");
        assertThat(migration).contains("idx_final_acceptance_share_package_archive_status");
    }
}
