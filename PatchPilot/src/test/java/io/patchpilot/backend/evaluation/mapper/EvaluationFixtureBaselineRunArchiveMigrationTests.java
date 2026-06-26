package io.patchpilot.backend.evaluation.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationFixtureBaselineRunArchiveMigrationTests {

    @Test
    void should_create_evaluation_fixture_baseline_run_archive_table() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V30__create_evaluation_fixture_baseline_run_archive.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("create table evaluation_fixture_baseline_run_archive");
        assertThat(sql).contains("id varchar(36) not null primary key");
        assertThat(sql).contains("status varchar(32) not null");
        assertThat(sql).contains("total_case_count int not null");
        assertThat(sql).contains("executed_case_count int not null");
        assertThat(sql).contains("passed_case_count int not null");
        assertThat(sql).contains("failed_case_count int not null");
        assertThat(sql).contains("skipped_case_count int not null");
        assertThat(sql).contains("created_at timestamp(6) not null");
        assertThat(sql).contains("side_effect_contract varchar(1024) not null");
        assertThat(sql).contains("next_action varchar(1024) not null");
        assertThat(sql).contains("report mediumtext not null");
        assertThat(sql).contains("idx_eval_fixture_baseline_archive_created");
        assertThat(sql).contains("(created_at)");
        assertThat(sql).contains("idx_eval_fixture_baseline_archive_status_created");
        assertThat(sql).contains("(status, created_at)");
    }
}
