package io.patchpilot.backend.demo.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLaunchAcceptanceCloseoutArchiveMigrationTests {

    @Test
    void should_create_launch_acceptance_closeout_archive_table() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V39__create_demo_launch_acceptance_closeout_archive.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("create table demo_launch_acceptance_closeout_archive");
        assertThat(sql).contains("id varchar(36) not null primary key");
        assertThat(sql).contains("status varchar(32) not null");
        assertThat(sql).contains("accepted boolean not null");
        assertThat(sql).contains("summary varchar(1024) not null");
        assertThat(sql).contains("session_id varchar(128) not null");
        assertThat(sql).contains("latest_task_id varchar(64)");
        assertThat(sql).contains("latest_pull_request_url varchar(512)");
        assertThat(sql).contains("latest_webhook_delivery_id varchar(128)");
        assertThat(sql).contains("evaluation_run_id varchar(128)");
        assertThat(sql).contains("latest_archive_id varchar(36)");
        assertThat(sql).contains("latest_delivery_receipt_id varchar(36)");
        assertThat(sql).contains("latest_delivery_target varchar(512)");
        assertThat(sql).contains("latest_delivery_channel varchar(128)");
        assertThat(sql).contains("delivery_receipt_freshness varchar(32) not null");
        assertThat(sql).contains("created_at timestamp(6) not null");
        assertThat(sql).contains("report mediumtext not null");
        assertThat(sql).contains("idx_demo_launch_acceptance_closeout_archive_created");
        assertThat(sql).contains("(created_at)");
        assertThat(sql).contains("idx_demo_launch_acceptance_closeout_archive_status_created");
        assertThat(sql).contains("(status, created_at)");
    }
}
