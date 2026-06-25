package io.patchpilot.backend.task.service;

import io.patchpilot.backend.safety.domain.RejectedTriggerCategory;
import io.patchpilot.backend.task.domain.bo.RecordFixTaskPreExecutionDecisionCommand;
import io.patchpilot.backend.task.domain.entity.FixTaskPreExecutionDecisionEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskPreExecutionDecisionVo;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationDecisionVo;
import io.patchpilot.backend.task.mapper.FixTaskPreExecutionDecisionMapper;
import io.patchpilot.backend.task.service.impl.MyBatisFixTaskPreExecutionDecisionService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MyBatisFixTaskPreExecutionDecisionServiceTests {

    private final FixTaskPreExecutionDecisionMapper decisionMapper = mock(FixTaskPreExecutionDecisionMapper.class);
    private final FixTaskPreExecutionDecisionService decisionService =
            new MyBatisFixTaskPreExecutionDecisionService(decisionMapper);

    @Test
    void should_insert_pre_execution_decision() {
        when(decisionMapper.insert(any(FixTaskPreExecutionDecisionEntity.class))).thenReturn(1);
        ArgumentCaptor<FixTaskPreExecutionDecisionEntity> entityCaptor =
                ArgumentCaptor.forClass(FixTaskPreExecutionDecisionEntity.class);

        FixTaskPreExecutionDecisionVo decision = decisionService.recordDecision(command(
                "task-123",
                "ISSUE_COMMENT",
                "safety gate accepted",
                Instant.parse("2026-06-20T04:01:00Z")
        ));

        verify(decisionMapper).insert(entityCaptor.capture());
        FixTaskPreExecutionDecisionEntity insertedEntity = entityCaptor.getValue();
        assertThat(insertedEntity.getId()).isNotBlank();
        assertThat(insertedEntity.getTaskId()).isEqualTo("task-123");
        assertThat(insertedEntity.getSource()).isEqualTo("ISSUE_COMMENT");
        assertThat(insertedEntity.getFinalDecision()).isEqualTo("ALLOWED");
        assertThat(insertedEntity.getSafetyAllowed()).isTrue();
        assertThat(insertedEntity.getSafetyReason()).isEqualTo("safety gate accepted");
        assertThat(insertedEntity.getActiveTaskReason()).isEqualTo("No active task exists for this issue");
        assertThat(insertedEntity.getQuarantineReason()).isEqualTo("not blocked before task creation");
        assertThat(insertedEntity.getRateLimitReason()).isEqualTo("not rate limited before task creation");
        assertThat(insertedEntity.getTriggerIntentReason()).isEqualTo("model accepted trigger: Issue context describes a concrete failing test");
        assertThat(insertedEntity.getIssueContextLoaded()).isTrue();
        assertThat(decision.id()).isEqualTo(insertedEntity.getId());
    }

    @Test
    void should_find_latest_pre_execution_decision() {
        FixTaskPreExecutionDecisionEntity older = entity(
                "decision-older",
                "task-123",
                "older safety",
                Instant.parse("2026-06-20T04:00:00Z")
        );
        FixTaskPreExecutionDecisionEntity newer = entity(
                "decision-newer",
                "task-123",
                "persisted safety",
                Instant.parse("2026-06-20T04:01:00Z")
        );
        when(decisionMapper.selectList(any())).thenReturn(List.of(older, newer));

        assertThat(decisionService.findLatestDecision("task-123"))
                .get()
                .satisfies(decision -> {
                    assertThat(decision.id()).isEqualTo("decision-newer");
                    assertThat(decision.safetyDecision().reason()).isEqualTo("persisted safety");
                });
    }

    private static RecordFixTaskPreExecutionDecisionCommand command(
            String taskId,
            String source,
            String safetyReason,
            Instant createdAt
    ) {
        return new RecordFixTaskPreExecutionDecisionCommand(
                taskId,
                source,
                "ALLOWED",
                new TriggerEvaluationDecisionVo(true, safetyReason, RejectedTriggerCategory.UNKNOWN),
                new TriggerEvaluationDecisionVo(true, "No active task exists for this issue", RejectedTriggerCategory.UNKNOWN),
                new TriggerEvaluationDecisionVo(true, "not blocked before task creation", RejectedTriggerCategory.UNKNOWN),
                new TriggerEvaluationDecisionVo(true, "not rate limited before task creation", RejectedTriggerCategory.UNKNOWN),
                new TriggerEvaluationDecisionVo(true, "model accepted trigger: Issue context describes a concrete failing test", RejectedTriggerCategory.UNKNOWN),
                true,
                createdAt
        );
    }

    private static FixTaskPreExecutionDecisionEntity entity(
            String id,
            String taskId,
            String safetyReason,
            Instant createdAt
    ) {
        FixTaskPreExecutionDecisionEntity entity = new FixTaskPreExecutionDecisionEntity();
        entity.setId(id);
        entity.setTaskId(taskId);
        entity.setSource("ISSUE_COMMENT");
        entity.setFinalDecision("ALLOWED");
        entity.setSafetyAllowed(true);
        entity.setSafetyReason(safetyReason);
        entity.setSafetyCategory(RejectedTriggerCategory.UNKNOWN);
        entity.setActiveTaskAllowed(true);
        entity.setActiveTaskReason("No active task exists for this issue");
        entity.setActiveTaskCategory(RejectedTriggerCategory.UNKNOWN);
        entity.setQuarantineAllowed(true);
        entity.setQuarantineReason("not blocked before task creation");
        entity.setQuarantineCategory(RejectedTriggerCategory.UNKNOWN);
        entity.setRateLimitAllowed(true);
        entity.setRateLimitReason("not rate limited before task creation");
        entity.setRateLimitCategory(RejectedTriggerCategory.UNKNOWN);
        entity.setTriggerIntentAllowed(true);
        entity.setTriggerIntentReason("model accepted trigger: Issue context describes a concrete failing test");
        entity.setTriggerIntentCategory(RejectedTriggerCategory.UNKNOWN);
        entity.setIssueContextLoaded(true);
        entity.setCreatedAt(createdAt);
        return entity;
    }
}
