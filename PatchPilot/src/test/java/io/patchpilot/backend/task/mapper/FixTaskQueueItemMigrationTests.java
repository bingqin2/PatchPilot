package io.patchpilot.backend.task.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskQueueItemMigrationTests {

    @Test
    void should_create_fix_task_queue_item_table() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V8__create_fix_task_queue_item.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("create table fix_task_queue_item");
        assertThat(sql).contains("id varchar(36) not null primary key");
        assertThat(sql).contains("task_id varchar(36) not null");
        assertThat(sql).contains("status varchar(32) not null");
        assertThat(sql).contains("attempt_count int not null");
        assertThat(sql).contains("last_error text null");
        assertThat(sql).contains("available_at timestamp(6) not null");
        assertThat(sql).contains("locked_at timestamp(6) null");
        assertThat(sql).contains("created_at timestamp(6) not null");
        assertThat(sql).contains("updated_at timestamp(6) not null");
        assertThat(sql).contains("idx_fix_task_queue_item_status_available");
        assertThat(sql).contains("(status, available_at)");
    }
}
