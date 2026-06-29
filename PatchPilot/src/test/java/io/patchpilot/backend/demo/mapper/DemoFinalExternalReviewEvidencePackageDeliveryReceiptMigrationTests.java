package io.patchpilot.backend.demo.mapper;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalExternalReviewEvidencePackageDeliveryReceiptMigrationTests {

    @Test
    void creates_final_external_review_package_delivery_receipt_table() throws Exception {
        String migration = Files.readString(Path.of(
                "src/main/resources/db/migration/V55__create_demo_final_external_review_evidence_package_delivery_receipt.sql"
        ));

        assertThat(migration).contains("CREATE TABLE IF NOT EXISTS demo_final_external_review_evidence_package_delivery_receipt");
        assertThat(migration).contains("id VARCHAR(64) PRIMARY KEY");
        assertThat(migration).contains("status VARCHAR(32) NOT NULL");
        assertThat(migration).contains("final_external_review_package_archive_status VARCHAR(32) NOT NULL");
        assertThat(migration).contains("final_external_review_package_archive_id VARCHAR(64) NOT NULL");
        assertThat(migration).contains("closeout_archive_id VARCHAR(64)");
        assertThat(migration).contains("completion_archive_id VARCHAR(64)");
        assertThat(migration).contains("completion_evidence_delivery_receipt_id VARCHAR(64)");
        assertThat(migration).contains("latest_task_id VARCHAR(64)");
        assertThat(migration).contains("latest_pull_request_url VARCHAR(1024)");
        assertThat(migration).contains("delivery_channel VARCHAR(64) NOT NULL");
        assertThat(migration).contains("delivery_target VARCHAR(512) NOT NULL");
        assertThat(migration).contains("operator VARCHAR(128) NOT NULL");
        assertThat(migration).contains("notes VARCHAR(1024) NOT NULL");
        assertThat(migration).contains("delivered_at TIMESTAMP(6) NOT NULL");
        assertThat(migration).contains("created_at TIMESTAMP(6) NOT NULL");
        assertThat(migration).contains("markdown_report MEDIUMTEXT NOT NULL");
        assertThat(migration).contains("idx_final_external_review_package_delivery_receipt_created_at");
        assertThat(migration).contains("idx_final_external_review_package_delivery_receipt_archive_created");
    }
}
