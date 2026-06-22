package io.patchpilot.backend.task.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskAdapterMetadataMigrationTests {

    @Test
    void should_add_adapter_metadata_columns_to_fix_task() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V12__add_fix_task_adapter_metadata.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("alter table fix_task");
        assertThat(sql).contains("add column language varchar(64) null");
        assertThat(sql).contains("add column build_system varchar(64) null");
        assertThat(sql).contains("add column verification_command varchar(512) null");
    }
}
