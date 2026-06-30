package io.patchpilot.backend.demo.mapper;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalExternalReviewReleaseBundleDeliveryReceiptMigrationTests {

    @Test
    void creates_final_external_review_release_bundle_delivery_receipt_table() throws Exception {
        String migration = Files.readString(Path.of(
                "src/main/resources/db/migration/V59__create_demo_final_external_review_release_bundle_delivery_receipt.sql"
        ));

        assertThat(migration)
                .contains("CREATE TABLE IF NOT EXISTS demo_final_external_review_release_bundle_delivery_receipt");
        assertThat(migration).contains("id VARCHAR(64) PRIMARY KEY");
        assertThat(migration).contains("status VARCHAR(32) NOT NULL");
        assertThat(migration).contains("release_bundle_archive_status VARCHAR(32) NOT NULL");
        assertThat(migration).contains("release_bundle_archive_id VARCHAR(64) NOT NULL");
        assertThat(migration).contains("latest_certificate_archive_id VARCHAR(64)");
        assertThat(migration).contains("latest_delivery_finalization_archive_id VARCHAR(64)");
        assertThat(migration).contains("latest_package_archive_id VARCHAR(64)");
        assertThat(migration).contains("latest_package_delivery_receipt_id VARCHAR(64)");
        assertThat(migration).contains("latest_task_id VARCHAR(64)");
        assertThat(migration).contains("latest_pull_request_url VARCHAR(1024)");
        assertThat(migration).contains("delivery_channel VARCHAR(128) NOT NULL");
        assertThat(migration).contains("delivery_target VARCHAR(512) NOT NULL");
        assertThat(migration).contains("operator VARCHAR(128) NOT NULL");
        assertThat(migration).contains("markdown_report MEDIUMTEXT NOT NULL");
        assertThat(migration).contains("idx_final_external_review_release_bundle_delivery_receipt_created_at");
        assertThat(migration).contains("idx_final_external_review_release_bundle_delivery_receipt_archive_id");
        assertThat(migration).contains("idx_final_external_review_release_bundle_delivery_receipt_status");
    }
}
