package io.patchpilot.backend.task.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskPatchReviewMigrationTests {

    @Test
    void should_create_fix_task_patch_review_table() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V19__create_fix_task_patch_review.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("create table fix_task_patch_review");
        assertThat(sql).contains("id varchar(36) not null primary key");
        assertThat(sql).contains("task_id varchar(36) not null");
        assertThat(sql).contains("decision varchar(32) not null");
        assertThat(sql).contains("reason text not null");
        assertThat(sql).contains("confidence varchar(32) not null");
        assertThat(sql).contains("required_follow_up text null");
        assertThat(sql).contains("edited_files text not null");
        assertThat(sql).contains("created_at timestamp(6) not null");
        assertThat(sql).contains("idx_fix_task_patch_review_task_created");
        assertThat(sql).contains("(task_id, created_at)");
    }
}
