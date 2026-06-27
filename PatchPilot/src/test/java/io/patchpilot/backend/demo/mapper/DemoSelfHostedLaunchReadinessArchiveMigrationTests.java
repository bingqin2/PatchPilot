package io.patchpilot.backend.demo.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class DemoSelfHostedLaunchReadinessArchiveMigrationTests {

    @Test
    void should_create_self_hosted_launch_readiness_archive_table() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V35__create_demo_self_hosted_launch_readiness_archive.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("create table demo_self_hosted_launch_readiness_archive");
        assertThat(sql).contains("id varchar(36) not null primary key");
        assertThat(sql).contains("status varchar(32) not null");
        assertThat(sql).contains("ready_to_launch boolean not null");
        assertThat(sql).contains("summary varchar(1024) not null");
        assertThat(sql).contains("ready_check_count int not null");
        assertThat(sql).contains("needs_attention_check_count int not null");
        assertThat(sql).contains("blocked_check_count int not null");
        assertThat(sql).contains("created_at timestamp(6) not null");
        assertThat(sql).contains("report mediumtext not null");
        assertThat(sql).contains("idx_demo_self_hosted_launch_readiness_archive_created");
        assertThat(sql).contains("(created_at)");
        assertThat(sql).contains("idx_demo_self_hosted_launch_readiness_archive_status_created");
        assertThat(sql).contains("(status, created_at)");
    }
}
