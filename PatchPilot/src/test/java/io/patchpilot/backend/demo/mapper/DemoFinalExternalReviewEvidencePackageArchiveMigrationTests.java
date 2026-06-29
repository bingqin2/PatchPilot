package io.patchpilot.backend.demo.mapper;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalExternalReviewEvidencePackageArchiveMigrationTests {

    @Test
    void creates_final_external_review_package_archive_table() throws Exception {
        String migration = Files.readString(Path.of(
                "src/main/resources/db/migration/V54__create_demo_final_external_review_evidence_package_archive.sql"
        ));

        assertThat(migration).contains("CREATE TABLE IF NOT EXISTS demo_final_external_review_evidence_package_archive");
        assertThat(migration).contains("id VARCHAR(64) PRIMARY KEY");
        assertThat(migration).contains("status VARCHAR(32) NOT NULL");
        assertThat(migration).contains("ready_for_external_review BOOLEAN NOT NULL");
        assertThat(migration).contains("latest_task_id VARCHAR(64)");
        assertThat(migration).contains("latest_pull_request_url VARCHAR(1024)");
        assertThat(migration).contains("final_acceptance_share_package_archive_id VARCHAR(64)");
        assertThat(migration).contains("completion_archive_id VARCHAR(64)");
        assertThat(migration).contains("completion_evidence_delivery_receipt_id VARCHAR(64)");
        assertThat(migration).contains("closeout_archive_id VARCHAR(64)");
        assertThat(migration).contains("evidence_notes_json JSON NOT NULL");
        assertThat(migration).contains("download_actions_json JSON NOT NULL");
        assertThat(migration).contains("side_effect_contract VARCHAR(1024) NOT NULL");
        assertThat(migration).contains("report MEDIUMTEXT NOT NULL");
        assertThat(migration).contains("generated_at TIMESTAMP(6) NOT NULL");
        assertThat(migration).contains("archived_at TIMESTAMP(6) NOT NULL");
        assertThat(migration).contains("idx_final_external_review_package_archive_archived_at");
        assertThat(migration).contains("idx_final_external_review_package_archive_status");
    }
}
