package io.patchpilot.backend.demo.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class DemoSessionArchiveMigrationTests {

    @Test
    void should_create_demo_session_archive_table() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V25__create_demo_session_archive.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("create table demo_session_archive");
        assertThat(sql).contains("id varchar(36) not null primary key");
        assertThat(sql).contains("session_id varchar(128) not null");
        assertThat(sql).contains("status varchar(32) not null");
        assertThat(sql).contains("summary varchar(1024) not null");
        assertThat(sql).contains("share_summary varchar(1024) not null");
        assertThat(sql).contains("recent_pull_request_url varchar(512) null");
        assertThat(sql).contains("created_at timestamp(6) not null");
        assertThat(sql).contains("report mediumtext not null");
        assertThat(sql).contains("idx_demo_session_archive_created");
        assertThat(sql).contains("(created_at)");
        assertThat(sql).contains("idx_demo_session_archive_session_created");
        assertThat(sql).contains("(session_id, created_at)");
    }
}
