package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.entity.FixTaskTestRunEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import io.patchpilot.backend.task.mapper.FixTaskTestRunMapper;
import io.patchpilot.backend.task.service.impl.MyBatisFixTaskTestRunService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisFixTaskTestRunServiceTests {

    private final FixTaskTestRunMapper testRunMapper = mock(FixTaskTestRunMapper.class);
    private final FixTaskTestRunService testRunService = new MyBatisFixTaskTestRunService(testRunMapper);

    @Test
    void should_insert_test_run() {
        when(testRunMapper.insert(any(FixTaskTestRunEntity.class))).thenReturn(1);
        ArgumentCaptor<FixTaskTestRunEntity> entityCaptor = ArgumentCaptor.forClass(FixTaskTestRunEntity.class);

        FixTaskTestRunVo testRun = testRunService.recordTestRun(
                "task-123",
                "./mvnw test",
                1,
                "test failed",
                Instant.parse("2026-06-19T08:00:00Z"),
                Instant.parse("2026-06-19T08:00:05Z")
        );

        verify(testRunMapper).insert(entityCaptor.capture());
        FixTaskTestRunEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isNotBlank();
        assertThat(insertedEntity.getTaskId()).isEqualTo("task-123");
        assertThat(insertedEntity.getCommand()).isEqualTo("./mvnw test");
        assertThat(insertedEntity.getExitCode()).isEqualTo(1);
        assertThat(insertedEntity.getOutput()).isEqualTo("test failed");
        assertThat(insertedEntity.getDurationMs()).isEqualTo(5000);
        assertThat(testRun.id()).isEqualTo(insertedEntity.getId());
    }

    @Test
    void should_list_test_runs_oldest_first() {
        FixTaskTestRunEntity newer = entity(
                "test-run-newer",
                "task-123",
                1,
                Instant.parse("2026-06-19T08:05:00Z")
        );
        FixTaskTestRunEntity older = entity(
                "test-run-older",
                "task-123",
                0,
                Instant.parse("2026-06-19T08:00:00Z")
        );
        when(testRunMapper.selectList(any())).thenReturn(List.of(newer, older));

        List<FixTaskTestRunVo> testRuns = testRunService.listTestRuns("task-123");

        assertThat(testRuns)
                .extracting(FixTaskTestRunVo::id)
                .containsExactly("test-run-older", "test-run-newer");
    }

    private static FixTaskTestRunEntity entity(String id, String taskId, int exitCode, Instant startedAt) {
        FixTaskTestRunEntity entity = new FixTaskTestRunEntity();
        entity.setId(id);
        entity.setTaskId(taskId);
        entity.setCommand("./mvnw test");
        entity.setExitCode(exitCode);
        entity.setOutput(exitCode == 0 ? "tests passed" : "test failed");
        entity.setStartedAt(startedAt);
        entity.setFinishedAt(startedAt.plusSeconds(5));
        entity.setDurationMs(5000);
        return entity;
    }
}
