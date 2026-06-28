package io.patchpilot.backend.task.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskEvidencePackageAcceptanceCloseoutArchiveMigrationTests {

    @Test
    void should_create_task_evidence_acceptance_closeout_archive_table() throws IOException {
        String migration = Files.readString(Path.of(
                "src/main/resources/db/migration/V43__create_fix_task_evidence_acceptance_closeout_archive.sql"
        )).toLowerCase();

        assertThat(migration).contains("create table fix_task_evidence_acceptance_closeout_archive");
        assertThat(migration).contains("status varchar(32) not null");
        assertThat(migration).contains("accepted boolean not null");
        assertThat(migration).contains("summary varchar(1024) not null");
        assertThat(migration).contains("latest_archive_id varchar(36)");
        assertThat(migration).contains("latest_task_id varchar(64)");
        assertThat(migration).contains("latest_delivery_receipt_id varchar(36)");
        assertThat(migration).contains("delivery_receipt_freshness varchar(32) not null");
        assertThat(migration).contains("report mediumtext not null");
        assertThat(migration).contains("idx_fix_task_evidence_closeout_created");
        assertThat(migration).contains("idx_fix_task_evidence_closeout_latest_archive");
        assertThat(migration).contains("idx_fix_task_evidence_closeout_latest_task");
    }
}
