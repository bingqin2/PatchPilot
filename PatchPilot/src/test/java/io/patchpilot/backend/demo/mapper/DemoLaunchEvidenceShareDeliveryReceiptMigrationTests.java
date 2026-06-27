package io.patchpilot.backend.demo.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLaunchEvidenceShareDeliveryReceiptMigrationTests {

    @Test
    void should_create_demo_launch_evidence_share_delivery_receipt_table() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V38__create_demo_launch_evidence_share_delivery_receipt.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("create table demo_launch_evidence_share_delivery_receipt");
        assertThat(sql).contains("id varchar(36) not null primary key");
        assertThat(sql).contains("status varchar(32) not null");
        assertThat(sql).contains("launch_evidence_archive_id varchar(36) not null");
        assertThat(sql).contains("session_id varchar(128) not null");
        assertThat(sql).contains("delivery_channel varchar(64) not null");
        assertThat(sql).contains("delivery_target varchar(512) not null");
        assertThat(sql).contains("operator varchar(128) not null");
        assertThat(sql).contains("notes varchar(1024) not null");
        assertThat(sql).contains("message_subject varchar(512) not null");
        assertThat(sql).contains("delivered_at timestamp(6) not null");
        assertThat(sql).contains("created_at timestamp(6) not null");
        assertThat(sql).contains("markdown_report mediumtext not null");
        assertThat(sql).contains("idx_demo_launch_evidence_share_delivery_receipt_created");
        assertThat(sql).contains("(created_at)");
        assertThat(sql).contains("idx_demo_launch_evidence_share_delivery_receipt_archive_created");
        assertThat(sql).contains("(launch_evidence_archive_id, created_at)");
    }
}
