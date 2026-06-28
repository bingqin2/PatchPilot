package io.patchpilot.backend.task.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskEvidencePackageShareDeliveryReceiptMigrationTests {

    @Test
    void should_create_task_evidence_share_delivery_receipt_table() throws IOException {
        String migration = Files.readString(Path.of(
                "src/main/resources/db/migration/V42__create_fix_task_evidence_share_delivery_receipt.sql"
        )).toLowerCase();

        assertThat(migration).contains("create table fix_task_evidence_share_delivery_receipt");
        assertThat(migration).contains("task_evidence_archive_id varchar(36) not null");
        assertThat(migration).contains("task_id varchar(64) not null");
        assertThat(migration).contains("repository_owner varchar(255) not null");
        assertThat(migration).contains("repository_name varchar(255) not null");
        assertThat(migration).contains("issue_number bigint not null");
        assertThat(migration).contains("delivery_channel varchar(128) not null");
        assertThat(migration).contains("delivery_target varchar(512) not null");
        assertThat(migration).contains("operator varchar(255) not null");
        assertThat(migration).contains("notes text");
        assertThat(migration).contains("markdown_report mediumtext not null");
        assertThat(migration).contains("idx_fix_task_evidence_delivery_receipt_created");
        assertThat(migration).contains("idx_fix_task_evidence_delivery_receipt_archive_created");
    }
}
