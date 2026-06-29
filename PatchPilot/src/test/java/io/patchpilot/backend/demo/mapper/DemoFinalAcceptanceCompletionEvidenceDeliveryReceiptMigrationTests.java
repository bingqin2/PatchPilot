package io.patchpilot.backend.demo.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptMigrationTests {

    @Test
    void should_create_demo_final_acceptance_completion_evidence_delivery_receipt_table() throws IOException {
        String sql = Files.readString(Path.of(
                "src/main/resources/db/migration/V52__create_demo_final_acceptance_completion_evidence_delivery_receipt.sql"
        )).toLowerCase(Locale.ROOT);

        assertThat(sql).contains("create table demo_final_acceptance_completion_evidence_delivery_receipt");
        assertThat(sql).contains("id varchar(36) not null primary key");
        assertThat(sql).contains("status varchar(32) not null");
        assertThat(sql).contains("ready_to_share boolean not null");
        assertThat(sql).contains("completion_evidence_bundle_status varchar(32) not null");
        assertThat(sql).contains("summary varchar(512) not null");
        assertThat(sql).contains("next_action varchar(512) not null");
        assertThat(sql).contains("latest_completion_archive_id varchar(36) not null");
        assertThat(sql).contains("latest_share_package_archive_id varchar(36) not null");
        assertThat(sql).contains("latest_delivery_receipt_id varchar(36) not null");
        assertThat(sql).contains("latest_task_id varchar(128) not null");
        assertThat(sql).contains("delivery_channel varchar(64) not null");
        assertThat(sql).contains("delivery_target varchar(512) not null");
        assertThat(sql).contains("operator varchar(128) not null");
        assertThat(sql).contains("notes varchar(1024) not null");
        assertThat(sql).contains("delivered_at timestamp(6) not null");
        assertThat(sql).contains("created_at timestamp(6) not null");
        assertThat(sql).contains("markdown_report mediumtext not null");
        assertThat(sql).contains("idx_demo_final_acceptance_completion_evidence_receipt_created");
        assertThat(sql).contains("(created_at)");
        assertThat(sql).contains("idx_demo_final_acceptance_completion_evidence_receipt_archive_created");
        assertThat(sql).contains("(latest_completion_archive_id, created_at)");
    }
}
