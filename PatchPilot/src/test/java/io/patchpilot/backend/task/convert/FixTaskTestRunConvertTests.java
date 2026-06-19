package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.entity.FixTaskTestRunEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskTestRunVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskTestRunConvertTests {

    @Test
    void should_convert_between_entity_and_vo() {
        Instant startedAt = Instant.parse("2026-06-19T08:00:00Z");
        Instant finishedAt = Instant.parse("2026-06-19T08:00:05Z");

        FixTaskTestRunEntity entity = FixTaskTestRunConvert.newEntity(
                "test-run-123",
                "task-123",
                "./mvnw test",
                1,
                "test failed",
                startedAt,
                finishedAt,
                5000
        );
        FixTaskTestRunVo vo = FixTaskTestRunConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("test-run-123");
        assertThat(entity.getTaskId()).isEqualTo("task-123");
        assertThat(entity.getCommand()).isEqualTo("./mvnw test");
        assertThat(entity.getExitCode()).isEqualTo(1);
        assertThat(entity.getOutput()).isEqualTo("test failed");
        assertThat(entity.getStartedAt()).isEqualTo(startedAt);
        assertThat(entity.getFinishedAt()).isEqualTo(finishedAt);
        assertThat(entity.getDurationMs()).isEqualTo(5000);
        assertThat(vo.id()).isEqualTo("test-run-123");
        assertThat(vo.taskId()).isEqualTo("task-123");
        assertThat(vo.command()).isEqualTo("./mvnw test");
        assertThat(vo.exitCode()).isEqualTo(1);
        assertThat(vo.output()).isEqualTo("test failed");
        assertThat(vo.startedAt()).isEqualTo(startedAt);
        assertThat(vo.finishedAt()).isEqualTo(finishedAt);
        assertThat(vo.durationMs()).isEqualTo(5000);
    }
}
