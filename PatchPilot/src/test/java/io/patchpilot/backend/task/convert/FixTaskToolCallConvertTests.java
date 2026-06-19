package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.entity.FixTaskToolCallEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskToolCallVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskToolCallConvertTests {

    @Test
    void should_convert_between_entity_and_vo() {
        Instant startedAt = Instant.parse("2026-06-19T09:00:00Z");
        Instant finishedAt = Instant.parse("2026-06-19T09:00:02Z");

        FixTaskToolCallEntity entity = FixTaskToolCallConvert.newEntity(
                "tool-call-123",
                "task-123",
                "CommitTool",
                "repositoryDir=/tmp/workspace/repo",
                "committed",
                true,
                startedAt,
                finishedAt,
                2000
        );
        FixTaskToolCallVo vo = FixTaskToolCallConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("tool-call-123");
        assertThat(entity.getTaskId()).isEqualTo("task-123");
        assertThat(entity.getToolName()).isEqualTo("CommitTool");
        assertThat(entity.getInputSummary()).isEqualTo("repositoryDir=/tmp/workspace/repo");
        assertThat(entity.getOutputSummary()).isEqualTo("committed");
        assertThat(entity.isSuccess()).isTrue();
        assertThat(entity.getStartedAt()).isEqualTo(startedAt);
        assertThat(entity.getFinishedAt()).isEqualTo(finishedAt);
        assertThat(entity.getDurationMs()).isEqualTo(2000);
        assertThat(vo.id()).isEqualTo("tool-call-123");
        assertThat(vo.taskId()).isEqualTo("task-123");
        assertThat(vo.toolName()).isEqualTo("CommitTool");
        assertThat(vo.inputSummary()).isEqualTo("repositoryDir=/tmp/workspace/repo");
        assertThat(vo.outputSummary()).isEqualTo("committed");
        assertThat(vo.success()).isTrue();
        assertThat(vo.startedAt()).isEqualTo(startedAt);
        assertThat(vo.finishedAt()).isEqualTo(finishedAt);
        assertThat(vo.durationMs()).isEqualTo(2000);
    }
}
