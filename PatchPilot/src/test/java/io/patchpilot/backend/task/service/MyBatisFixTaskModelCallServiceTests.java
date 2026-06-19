package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.entity.FixTaskModelCallEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskModelCallVo;
import io.patchpilot.backend.task.mapper.FixTaskModelCallMapper;
import io.patchpilot.backend.task.service.impl.MyBatisFixTaskModelCallService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisFixTaskModelCallServiceTests {

    private final FixTaskModelCallMapper modelCallMapper = mock(FixTaskModelCallMapper.class);
    private final FixTaskModelCallService modelCallService = new MyBatisFixTaskModelCallService(modelCallMapper);

    @Test
    void should_insert_model_call() {
        when(modelCallMapper.insert(any(FixTaskModelCallEntity.class))).thenReturn(1);
        ArgumentCaptor<FixTaskModelCallEntity> entityCaptor = ArgumentCaptor.forClass(FixTaskModelCallEntity.class);

        FixTaskModelCallVo modelCall = modelCallService.recordModelCall(
                "task-123",
                "openai",
                "gpt-4.1-mini",
                "Fix issue",
                "Patch generated",
                100,
                50,
                true,
                null,
                Instant.parse("2026-06-20T01:00:00Z"),
                Instant.parse("2026-06-20T01:00:04Z")
        );

        verify(modelCallMapper).insert(entityCaptor.capture());
        FixTaskModelCallEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isNotBlank();
        assertThat(insertedEntity.getTaskId()).isEqualTo("task-123");
        assertThat(insertedEntity.getProvider()).isEqualTo("openai");
        assertThat(insertedEntity.getModel()).isEqualTo("gpt-4.1-mini");
        assertThat(insertedEntity.getPromptSummary()).isEqualTo("Fix issue");
        assertThat(insertedEntity.getResponseSummary()).isEqualTo("Patch generated");
        assertThat(insertedEntity.getPromptTokens()).isEqualTo(100);
        assertThat(insertedEntity.getCompletionTokens()).isEqualTo(50);
        assertThat(insertedEntity.getTotalTokens()).isEqualTo(150);
        assertThat(insertedEntity.isSuccess()).isTrue();
        assertThat(insertedEntity.getErrorMessage()).isNull();
        assertThat(insertedEntity.getDurationMs()).isEqualTo(4000);
        assertThat(modelCall.id()).isEqualTo(insertedEntity.getId());
    }

    @Test
    void should_list_model_calls_oldest_first() {
        FixTaskModelCallEntity newer = entity(
                "model-call-newer",
                "task-123",
                "Apply patch",
                false,
                Instant.parse("2026-06-20T01:05:00Z")
        );
        FixTaskModelCallEntity older = entity(
                "model-call-older",
                "task-123",
                "Create fix plan",
                true,
                Instant.parse("2026-06-20T01:00:00Z")
        );
        when(modelCallMapper.selectList(any())).thenReturn(List.of(newer, older));

        List<FixTaskModelCallVo> modelCalls = modelCallService.listModelCalls("task-123");

        assertThat(modelCalls)
                .extracting(FixTaskModelCallVo::id)
                .containsExactly("model-call-older", "model-call-newer");
    }

    private static FixTaskModelCallEntity entity(
            String id,
            String taskId,
            String promptSummary,
            boolean success,
            Instant startedAt
    ) {
        FixTaskModelCallEntity entity = new FixTaskModelCallEntity();
        entity.setId(id);
        entity.setTaskId(taskId);
        entity.setProvider("openai");
        entity.setModel("gpt-4.1-mini");
        entity.setPromptSummary(promptSummary);
        entity.setResponseSummary(success ? "ok" : null);
        entity.setPromptTokens(100);
        entity.setCompletionTokens(success ? 40 : 0);
        entity.setTotalTokens(success ? 140 : 100);
        entity.setSuccess(success);
        entity.setErrorMessage(success ? null : "model timeout");
        entity.setStartedAt(startedAt);
        entity.setFinishedAt(startedAt.plusSeconds(2));
        entity.setDurationMs(2000);
        return entity;
    }
}
