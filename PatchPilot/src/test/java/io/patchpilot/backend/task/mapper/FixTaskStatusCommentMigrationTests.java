package io.patchpilot.backend.task.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskStatusCommentMigrationTests {

    @Test
    void should_add_status_comment_columns_to_fix_task_table() throws IOException {
        String migration = Files.readString(Path.of(
                "src/main/resources/db/migration/V3__add_fix_task_status_comment.sql"
        ));

        assertThat(migration).contains("ALTER TABLE fix_task");
        assertThat(migration).contains("ADD COLUMN status_comment_id bigint null");
        assertThat(migration).contains("ADD COLUMN status_comment_url varchar(2048) null");
    }
}
