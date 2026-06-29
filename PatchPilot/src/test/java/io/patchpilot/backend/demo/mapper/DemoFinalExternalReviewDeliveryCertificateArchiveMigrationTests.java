package io.patchpilot.backend.demo.mapper;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalExternalReviewDeliveryCertificateArchiveMigrationTests {

    @Test
    void creates_final_external_review_delivery_certificate_archive_table() throws Exception {
        String migration = Files.readString(Path.of(
                "src/main/resources/db/migration/V57__create_demo_final_external_review_delivery_certificate_archive.sql"
        ));

        assertThat(migration)
                .contains("CREATE TABLE IF NOT EXISTS demo_final_external_review_delivery_certificate_archive");
        assertThat(migration).contains("id VARCHAR(64) PRIMARY KEY");
        assertThat(migration).contains("status VARCHAR(32) NOT NULL");
        assertThat(migration).contains("certified BOOLEAN NOT NULL");
        assertThat(migration).contains("latest_delivery_finalization_archive_id VARCHAR(64)");
        assertThat(migration).contains("latest_package_archive_id VARCHAR(64)");
        assertThat(migration).contains("latest_delivery_receipt_id VARCHAR(64)");
        assertThat(migration).contains("latest_task_id VARCHAR(64)");
        assertThat(migration).contains("latest_pull_request_url VARCHAR(1024)");
        assertThat(migration).contains("checks_json JSON NOT NULL");
        assertThat(migration).contains("evidence_notes_json JSON NOT NULL");
        assertThat(migration).contains("download_actions_json JSON NOT NULL");
        assertThat(migration).contains("side_effect_contract VARCHAR(1024) NOT NULL");
        assertThat(migration).contains("report MEDIUMTEXT NOT NULL");
        assertThat(migration).contains("generated_at TIMESTAMP(6) NOT NULL");
        assertThat(migration).contains("archived_at TIMESTAMP(6) NOT NULL");
        assertThat(migration).contains("idx_final_external_review_delivery_certificate_archive_archived_at");
        assertThat(migration).contains("idx_final_external_review_delivery_certificate_archive_status");
        assertThat(migration).contains("idx_final_external_review_delivery_certificate_archive_certified");
    }
}
