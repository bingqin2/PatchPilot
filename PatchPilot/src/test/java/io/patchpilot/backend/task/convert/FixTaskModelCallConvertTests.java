package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.entity.FixTaskModelCallEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskModelCallVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FixTaskModelCallConvertTests {

    @Test
    void should_convert_between_entity_and_vo() {
        Instant startedAt = Instant.parse("2026-06-20T01:00:00Z");
        Instant finishedAt = Instant.parse("2026-06-20T01:00:04Z");

        FixTaskModelCallEntity entity = FixTaskModelCallConvert.newEntity(
                "model-call-123",
                "task-123",
                "openai",
                "gpt-4.1-mini",
                "Fix failing calculator test",
                "Changed Calculator#add",
                120,
                80,
                true,
                null,
                startedAt,
                finishedAt,
                4000
        );
        FixTaskModelCallVo vo = FixTaskModelCallConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("model-call-123");
        assertThat(entity.getTaskId()).isEqualTo("task-123");
        assertThat(entity.getProvider()).isEqualTo("openai");
        assertThat(entity.getModel()).isEqualTo("gpt-4.1-mini");
        assertThat(entity.getPromptSummary()).isEqualTo("Fix failing calculator test");
        assertThat(entity.getResponseSummary()).isEqualTo("Changed Calculator#add");
        assertThat(entity.getPromptTokens()).isEqualTo(120);
        assertThat(entity.getCompletionTokens()).isEqualTo(80);
        assertThat(entity.getTotalTokens()).isEqualTo(200);
        assertThat(entity.isSuccess()).isTrue();
        assertThat(entity.getErrorMessage()).isNull();
        assertThat(entity.getStartedAt()).isEqualTo(startedAt);
        assertThat(entity.getFinishedAt()).isEqualTo(finishedAt);
        assertThat(entity.getDurationMs()).isEqualTo(4000);
        assertThat(vo.id()).isEqualTo("model-call-123");
        assertThat(vo.taskId()).isEqualTo("task-123");
        assertThat(vo.provider()).isEqualTo("openai");
        assertThat(vo.model()).isEqualTo("gpt-4.1-mini");
        assertThat(vo.promptSummary()).isEqualTo("Fix failing calculator test");
        assertThat(vo.responseSummary()).isEqualTo("Changed Calculator#add");
        assertThat(vo.promptTokens()).isEqualTo(120);
        assertThat(vo.completionTokens()).isEqualTo(80);
        assertThat(vo.totalTokens()).isEqualTo(200);
        assertThat(vo.success()).isTrue();
        assertThat(vo.errorMessage()).isNull();
        assertThat(vo.startedAt()).isEqualTo(startedAt);
        assertThat(vo.finishedAt()).isEqualTo(finishedAt);
        assertThat(vo.durationMs()).isEqualTo(4000);
    }
}
