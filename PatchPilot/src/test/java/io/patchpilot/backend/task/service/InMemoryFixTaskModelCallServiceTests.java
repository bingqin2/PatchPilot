package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskModelCallVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskModelCallService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryFixTaskModelCallServiceTests {

    private final FixTaskModelCallService modelCallService = new InMemoryFixTaskModelCallService();

    @Test
    void should_record_and_list_model_calls_oldest_first() {
        FixTaskModelCallVo older = modelCallService.recordModelCall(
                "task-123",
                "openai",
                "gpt-4.1-mini",
                "Fix calculator bug",
                "Patch plan created",
                100,
                40,
                true,
                null,
                Instant.parse("2026-06-20T01:00:00Z"),
                Instant.parse("2026-06-20T01:00:02Z")
        );
        FixTaskModelCallVo newer = modelCallService.recordModelCall(
                "task-123",
                "openai",
                "gpt-4.1-mini",
                "Apply patch",
                null,
                90,
                0,
                false,
                "model timeout",
                Instant.parse("2026-06-20T01:02:00Z"),
                Instant.parse("2026-06-20T01:02:03Z")
        );
        modelCallService.recordModelCall(
                "task-other",
                "openai",
                "gpt-4.1-mini",
                "Other task",
                "ignored",
                10,
                5,
                true,
                null,
                Instant.parse("2026-06-20T01:03:00Z"),
                Instant.parse("2026-06-20T01:03:01Z")
        );

        List<FixTaskModelCallVo> modelCalls = modelCallService.listModelCalls("task-123");

        assertThat(older.id()).isNotBlank();
        assertThat(older.totalTokens()).isEqualTo(140);
        assertThat(older.durationMs()).isEqualTo(2000);
        assertThat(newer.totalTokens()).isEqualTo(90);
        assertThat(newer.durationMs()).isEqualTo(3000);
        assertThat(modelCalls)
                .extracting(FixTaskModelCallVo::promptSummary)
                .containsExactly("Fix calculator bug", "Apply patch");
        assertThat(modelCalls)
                .extracting(FixTaskModelCallVo::success)
                .containsExactly(true, false);
    }
}
