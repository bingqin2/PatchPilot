package io.patchpilot.backend.task.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskResultMigrationTests {

    @Test
    void should_add_result_metadata_columns_to_fix_task() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V2__add_fix_task_result_fields.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("alter table fix_task");
        assertThat(sql).contains("add column pull_request_url varchar(2048) null");
        assertThat(sql).contains("add column completed_at timestamp(6) null");
        assertThat(sql).contains("add column updated_at timestamp(6) null");
    }
}
