package io.patchpilot.backend.demo.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLaunchAcceptanceCertificateArchiveMigrationTests {

    @Test
    void should_create_launch_acceptance_certificate_archive_table() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V40__create_demo_launch_acceptance_certificate_archive.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("create table demo_launch_acceptance_certificate_archive");
        assertThat(sql).contains("id varchar(36) not null primary key");
        assertThat(sql).contains("status varchar(32) not null");
        assertThat(sql).contains("certified boolean not null");
        assertThat(sql).contains("summary varchar(1024) not null");
        assertThat(sql).contains("next_action varchar(1024) not null");
        assertThat(sql).contains("archive_count int not null");
        assertThat(sql).contains("latest_closeout_archive_id varchar(36)");
        assertThat(sql).contains("latest_launch_evidence_archive_id varchar(36)");
        assertThat(sql).contains("latest_delivery_receipt_id varchar(36)");
        assertThat(sql).contains("latest_session_id varchar(128)");
        assertThat(sql).contains("latest_task_id varchar(64)");
        assertThat(sql).contains("latest_pull_request_url varchar(512)");
        assertThat(sql).contains("latest_webhook_delivery_id varchar(128)");
        assertThat(sql).contains("evaluation_run_id varchar(128)");
        assertThat(sql).contains("latest_delivery_target varchar(512)");
        assertThat(sql).contains("latest_delivery_channel varchar(128)");
        assertThat(sql).contains("delivery_receipt_freshness varchar(32) not null");
        assertThat(sql).contains("latest_archived_at timestamp(6)");
        assertThat(sql).contains("generated_at timestamp(6) not null");
        assertThat(sql).contains("archived_at timestamp(6) not null");
        assertThat(sql).contains("download_actions_json mediumtext not null");
        assertThat(sql).contains("report mediumtext not null");
        assertThat(sql).contains("idx_demo_launch_acceptance_certificate_archive_archived");
        assertThat(sql).contains("(archived_at)");
        assertThat(sql).contains("idx_demo_launch_acceptance_certificate_archive_status_archived");
        assertThat(sql).contains("(status, archived_at)");
    }

    @Test
    void should_add_final_handoff_archive_proof_columns_to_launch_acceptance_certificate_archive() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V48__add_launch_acceptance_certificate_final_handoff_archive_proof.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("alter table demo_launch_acceptance_certificate_archive");
        assertThat(sql).contains("final_handoff_report_package_archive_status varchar(32) not null default 'needs_attention'");
        assertThat(sql).contains("final_handoff_report_package_archive_ready boolean not null default false");
        assertThat(sql).contains("final_handoff_report_package_archive_id varchar(128)");
        assertThat(sql).contains("final_handoff_report_package_archive_summary varchar(1024) not null default 'no final handoff report package archive evidence recorded.'");
    }
}
