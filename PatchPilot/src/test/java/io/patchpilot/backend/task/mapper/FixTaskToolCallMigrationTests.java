package io.patchpilot.backend.task.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskToolCallMigrationTests {

    @Test
    void should_create_fix_task_tool_call_table() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V7__create_fix_task_tool_call.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("create table fix_task_tool_call");
        assertThat(sql).contains("id varchar(36) not null primary key");
        assertThat(sql).contains("task_id varchar(36) not null");
        assertThat(sql).contains("tool_name varchar(128) not null");
        assertThat(sql).contains("input_summary varchar(1024) null");
        assertThat(sql).contains("output_summary text null");
        assertThat(sql).contains("success boolean not null");
        assertThat(sql).contains("started_at timestamp(6) not null");
        assertThat(sql).contains("finished_at timestamp(6) not null");
        assertThat(sql).contains("duration_ms bigint not null");
        assertThat(sql).contains("idx_fix_task_tool_call_task_started");
        assertThat(sql).contains("(task_id, started_at)");
    }
}
