package io.patchpilot.backend.task.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskEvidencePackageAcceptanceCertificateArchiveMigrationTests {

    @Test
    void should_create_task_evidence_acceptance_certificate_archive_table() throws IOException {
        String migration = Files.readString(Path.of(
                "src/main/resources/db/migration/V44__create_fix_task_evidence_acceptance_certificate_archive.sql"
        )).toLowerCase();

        assertThat(migration).contains("create table fix_task_evidence_acceptance_certificate_archive");
        assertThat(migration).contains("status varchar(32) not null");
        assertThat(migration).contains("certified boolean not null");
        assertThat(migration).contains("summary varchar(1024) not null");
        assertThat(migration).contains("next_action varchar(1024) not null");
        assertThat(migration).contains("latest_closeout_archive_id varchar(36)");
        assertThat(migration).contains("latest_evidence_archive_id varchar(36)");
        assertThat(migration).contains("latest_delivery_receipt_id varchar(36)");
        assertThat(migration).contains("delivery_receipt_freshness varchar(32) not null");
        assertThat(migration).contains("download_actions_json mediumtext not null");
        assertThat(migration).contains("report mediumtext not null");
        assertThat(migration).contains("idx_fix_task_evidence_certificate_archived");
        assertThat(migration).contains("idx_fix_task_evidence_certificate_closeout");
        assertThat(migration).contains("idx_fix_task_evidence_certificate_task");
    }
}
