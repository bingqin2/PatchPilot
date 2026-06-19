package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskToolCallVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskToolCallService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryFixTaskToolCallServiceTests {

    private final FixTaskToolCallService toolCallService = new InMemoryFixTaskToolCallService();

    @Test
    void should_record_and_list_tool_calls_oldest_first() {
        FixTaskToolCallVo older = toolCallService.recordToolCall(
                "task-123",
                "PatchWorkflow",
                "repositoryDir=/tmp/workspace/repo",
                "patch applied",
                true,
                Instant.parse("2026-06-19T09:00:00Z"),
                Instant.parse("2026-06-19T09:00:01Z")
        );
        FixTaskToolCallVo newer = toolCallService.recordToolCall(
                "task-123",
                "CommitTool",
                "message=PatchPilot task task-123",
                "git commit failed: nothing to commit",
                false,
                Instant.parse("2026-06-19T09:01:00Z"),
                Instant.parse("2026-06-19T09:01:03Z")
        );
        toolCallService.recordToolCall(
                "task-other",
                "PushTool",
                "branch=patchpilot/task-other",
                "pushed",
                true,
                Instant.parse("2026-06-19T09:02:00Z"),
                Instant.parse("2026-06-19T09:02:01Z")
        );

        List<FixTaskToolCallVo> toolCalls = toolCallService.listToolCalls("task-123");

        assertThat(older.id()).isNotBlank();
        assertThat(older.durationMs()).isEqualTo(1000);
        assertThat(newer.durationMs()).isEqualTo(3000);
        assertThat(toolCalls)
                .extracting(FixTaskToolCallVo::toolName)
                .containsExactly("PatchWorkflow", "CommitTool");
        assertThat(toolCalls)
                .extracting(FixTaskToolCallVo::success)
                .containsExactly(true, false);
    }
}
