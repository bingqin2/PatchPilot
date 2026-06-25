package io.patchpilot.backend.task.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskPreExecutionDecisionMigrationTests {

    @Test
    void should_create_fix_task_pre_execution_decision_table() throws IOException {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V28__create_fix_task_pre_execution_decision.sql"))
                .toLowerCase(Locale.ROOT);

        assertThat(sql).contains("create table fix_task_pre_execution_decision");
        assertThat(sql).contains("id varchar(36) not null primary key");
        assertThat(sql).contains("task_id varchar(36) not null");
        assertThat(sql).contains("source varchar(32) not null");
        assertThat(sql).contains("final_decision varchar(32) not null");
        assertThat(sql).contains("safety_allowed boolean not null");
        assertThat(sql).contains("safety_reason text not null");
        assertThat(sql).contains("safety_category varchar(64) not null");
        assertThat(sql).contains("active_task_allowed boolean not null");
        assertThat(sql).contains("active_task_reason text not null");
        assertThat(sql).contains("active_task_category varchar(64) not null");
        assertThat(sql).contains("quarantine_allowed boolean not null");
        assertThat(sql).contains("quarantine_reason text not null");
        assertThat(sql).contains("quarantine_category varchar(64) not null");
        assertThat(sql).contains("rate_limit_allowed boolean not null");
        assertThat(sql).contains("rate_limit_reason text not null");
        assertThat(sql).contains("rate_limit_category varchar(64) not null");
        assertThat(sql).contains("trigger_intent_allowed boolean not null");
        assertThat(sql).contains("trigger_intent_reason text not null");
        assertThat(sql).contains("trigger_intent_category varchar(64) not null");
        assertThat(sql).contains("issue_context_loaded boolean not null");
        assertThat(sql).contains("created_at timestamp(6) not null");
        assertThat(sql).contains("idx_fix_task_pre_execution_decision_task_created");
        assertThat(sql).contains("(task_id, created_at)");
    }
}
