package io.patchpilot.backend.task.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskTestRunOutputStorageMigrationTests {

    @Test
    void should_expand_test_run_output_to_mediumtext() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V10__expand_fix_task_test_run_output.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("alter table fix_task_test_run");
        assertThat(sql).contains("modify column output mediumtext null");
    }
}
