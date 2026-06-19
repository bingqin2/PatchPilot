package io.patchpilot.backend.task.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskTimelineMigrationTests {

    @Test
    void should_create_fix_task_timeline_event_table() throws IOException {
        String migration = Files.readString(Path.of(
                "src/main/resources/db/migration/V4__create_fix_task_timeline_event.sql"
        ));

        assertThat(migration).contains("CREATE TABLE fix_task_timeline_event");
        assertThat(migration).contains("id varchar(36) not null primary key");
        assertThat(migration).contains("task_id varchar(36) not null");
        assertThat(migration).contains("event_type varchar(64) not null");
        assertThat(migration).contains("message varchar(2048) null");
        assertThat(migration).contains("created_at timestamp(6) not null");
        assertThat(migration).contains("idx_fix_task_timeline_event_task_created");
    }
}
