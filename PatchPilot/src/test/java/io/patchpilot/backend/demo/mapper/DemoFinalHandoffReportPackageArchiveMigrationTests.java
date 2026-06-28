package io.patchpilot.backend.demo.mapper;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalHandoffReportPackageArchiveMigrationTests {

    @Test
    void creates_final_handoff_report_package_archive_table() throws Exception {
        String migration = Files.readString(Path.of(
                "src/main/resources/db/migration/V45__create_demo_final_handoff_report_package_archive.sql"
        ));

        assertThat(migration).contains("CREATE TABLE IF NOT EXISTS demo_final_handoff_report_package_archive");
        assertThat(migration).contains("id VARCHAR(64) PRIMARY KEY");
        assertThat(migration).contains("status VARCHAR(32) NOT NULL");
        assertThat(migration).contains("download_ready BOOLEAN NOT NULL");
        assertThat(migration).contains("latest_archive_id VARCHAR(64)");
        assertThat(migration).contains("latest_session_id VARCHAR(128)");
        assertThat(migration).contains("latest_delivery_receipt_id VARCHAR(64)");
        assertThat(migration).contains("task_certificate_archive_id VARCHAR(64)");
        assertThat(migration).contains("task_certificate_ready BOOLEAN NOT NULL");
        assertThat(migration).contains("readiness_checks_json JSON NOT NULL");
        assertThat(migration).contains("required_attachments_json JSON NOT NULL");
        assertThat(migration).contains("pre_send_checks_json JSON NOT NULL");
        assertThat(migration).contains("evidence_notes_json JSON NOT NULL");
        assertThat(migration).contains("source_reports_json JSON NOT NULL");
        assertThat(migration).contains("report MEDIUMTEXT NOT NULL");
        assertThat(migration).contains("generated_at TIMESTAMP(6) NOT NULL");
        assertThat(migration).contains("archived_at TIMESTAMP(6) NOT NULL");
        assertThat(migration).contains("idx_final_handoff_report_package_archive_archived_at");
        assertThat(migration).contains("idx_final_handoff_report_package_archive_status");
    }
}
