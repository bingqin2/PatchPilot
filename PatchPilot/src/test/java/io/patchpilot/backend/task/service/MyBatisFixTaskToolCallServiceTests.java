package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.entity.FixTaskToolCallEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskToolCallVo;
import io.patchpilot.backend.task.mapper.FixTaskToolCallMapper;
import io.patchpilot.backend.task.service.impl.MyBatisFixTaskToolCallService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisFixTaskToolCallServiceTests {

    private final FixTaskToolCallMapper toolCallMapper = mock(FixTaskToolCallMapper.class);
    private final FixTaskToolCallService toolCallService = new MyBatisFixTaskToolCallService(toolCallMapper);

    @Test
    void should_insert_tool_call() {
        when(toolCallMapper.insert(any(FixTaskToolCallEntity.class))).thenReturn(1);
        ArgumentCaptor<FixTaskToolCallEntity> entityCaptor = ArgumentCaptor.forClass(FixTaskToolCallEntity.class);

        FixTaskToolCallVo toolCall = toolCallService.recordToolCall(
                "task-123",
                "CommitTool",
                "repositoryDir=/tmp/workspace/repo",
                "committed",
                true,
                Instant.parse("2026-06-19T09:00:00Z"),
                Instant.parse("2026-06-19T09:00:02Z")
        );

        verify(toolCallMapper).insert(entityCaptor.capture());
        FixTaskToolCallEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isNotBlank();
        assertThat(insertedEntity.getTaskId()).isEqualTo("task-123");
        assertThat(insertedEntity.getToolName()).isEqualTo("CommitTool");
        assertThat(insertedEntity.getInputSummary()).isEqualTo("repositoryDir=/tmp/workspace/repo");
        assertThat(insertedEntity.getOutputSummary()).isEqualTo("committed");
        assertThat(insertedEntity.isSuccess()).isTrue();
        assertThat(insertedEntity.getDurationMs()).isEqualTo(2000);
        assertThat(toolCall.id()).isEqualTo(insertedEntity.getId());
    }

    @Test
    void should_list_tool_calls_oldest_first() {
        FixTaskToolCallEntity newer = entity(
                "tool-call-newer",
                "task-123",
                "CommitTool",
                false,
                Instant.parse("2026-06-19T09:05:00Z")
        );
        FixTaskToolCallEntity older = entity(
                "tool-call-older",
                "task-123",
                "PatchWorkflow",
                true,
                Instant.parse("2026-06-19T09:00:00Z")
        );
        when(toolCallMapper.selectList(any())).thenReturn(List.of(newer, older));

        List<FixTaskToolCallVo> toolCalls = toolCallService.listToolCalls("task-123");

        assertThat(toolCalls)
                .extracting(FixTaskToolCallVo::id)
                .containsExactly("tool-call-older", "tool-call-newer");
    }

    private static FixTaskToolCallEntity entity(
            String id,
            String taskId,
            String toolName,
            boolean success,
            Instant startedAt
    ) {
        FixTaskToolCallEntity entity = new FixTaskToolCallEntity();
        entity.setId(id);
        entity.setTaskId(taskId);
        entity.setToolName(toolName);
        entity.setInputSummary("input");
        entity.setOutputSummary(success ? "ok" : "failed");
        entity.setSuccess(success);
        entity.setStartedAt(startedAt);
        entity.setFinishedAt(startedAt.plusSeconds(2));
        entity.setDurationMs(2000);
        return entity;
    }
}
