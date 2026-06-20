package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskTestRunService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryFixTaskTestRunServiceTests {

    private final FixTaskTestRunService testRunService = new InMemoryFixTaskTestRunService();

    @Test
    void should_record_and_list_test_runs_oldest_first() {
        FixTaskTestRunVo older = testRunService.recordTestRun(
                "task-123",
                "./mvnw test",
                0,
                "tests passed",
                Instant.parse("2026-06-19T08:00:00Z"),
                Instant.parse("2026-06-19T08:00:05Z")
        );
        FixTaskTestRunVo newer = testRunService.recordTestRun(
                "task-123",
                "./mvnw test",
                1,
                "test failed",
                Instant.parse("2026-06-19T08:01:00Z"),
                Instant.parse("2026-06-19T08:01:03Z")
        );
        testRunService.recordTestRun(
                "task-other",
                "./mvnw test",
                0,
                "other",
                Instant.parse("2026-06-19T08:02:00Z"),
                Instant.parse("2026-06-19T08:02:01Z")
        );

        List<FixTaskTestRunVo> testRuns = testRunService.listTestRuns("task-123");

        assertThat(older.id()).isNotBlank();
        assertThat(older.durationMs()).isEqualTo(5000);
        assertThat(newer.durationMs()).isEqualTo(3000);
        assertThat(testRuns)
                .extracting(FixTaskTestRunVo::output)
                .containsExactly("tests passed", "test failed");
    }

    @Test
    void should_truncate_test_output_before_recording() {
        FixTaskTestRunVo testRun = testRunService.recordTestRun(
                "task-123",
                "./mvnw test",
                1,
                "x".repeat(70_000),
                Instant.parse("2026-06-19T08:00:00Z"),
                Instant.parse("2026-06-19T08:00:05Z")
        );

        assertThat(testRun.output()).hasSizeLessThanOrEqualTo(60_000);
        assertThat(testRun.output()).contains("[truncated ");
    }
}
