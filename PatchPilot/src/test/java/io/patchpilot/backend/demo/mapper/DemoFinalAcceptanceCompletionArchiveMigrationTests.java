package io.patchpilot.backend.demo.mapper;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalAcceptanceCompletionArchiveMigrationTests {

    @Test
    void creates_final_acceptance_completion_archive_table() throws Exception {
        String migration = Files.readString(Path.of(
                "src/main/resources/db/migration/V51__create_demo_final_acceptance_completion_archive.sql"
        ));

        assertThat(migration).contains("CREATE TABLE IF NOT EXISTS demo_final_acceptance_completion_archive");
        assertThat(migration).contains("id VARCHAR(64) PRIMARY KEY");
        assertThat(migration).contains("status VARCHAR(32) NOT NULL");
        assertThat(migration).contains("finalized BOOLEAN NOT NULL");
        assertThat(migration).contains("latest_archive_id VARCHAR(64)");
        assertThat(migration).contains("latest_task_id VARCHAR(64)");
        assertThat(migration).contains("latest_delivery_receipt_id VARCHAR(64)");
        assertThat(migration).contains("latest_delivery_target VARCHAR(512)");
        assertThat(migration).contains("latest_delivery_channel VARCHAR(64)");
        assertThat(migration).contains("delivery_receipt_freshness VARCHAR(32) NOT NULL");
        assertThat(migration).contains("delivery_receipt_fresh BOOLEAN NOT NULL");
        assertThat(migration).contains("evidence_notes_json JSON NOT NULL");
        assertThat(migration).contains("report MEDIUMTEXT NOT NULL");
        assertThat(migration).contains("generated_at TIMESTAMP(6) NOT NULL");
        assertThat(migration).contains("archived_at TIMESTAMP(6) NOT NULL");
        assertThat(migration).contains("idx_final_acceptance_completion_archive_archived_at");
        assertThat(migration).contains("idx_final_acceptance_completion_archive_status");
    }
}
