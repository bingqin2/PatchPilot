package io.patchpilot.backend.task.service;

import io.patchpilot.backend.safety.domain.RejectedTriggerCategory;
import io.patchpilot.backend.task.domain.bo.RecordFixTaskPreExecutionDecisionCommand;
import io.patchpilot.backend.task.domain.vo.FixTaskPreExecutionDecisionVo;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationDecisionVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskPreExecutionDecisionService;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryFixTaskPreExecutionDecisionServiceTests {

    private final FixTaskPreExecutionDecisionService decisionService = new InMemoryFixTaskPreExecutionDecisionService();

    @Test
    void should_record_and_return_latest_pre_execution_decision() {
        decisionService.recordDecision(command(
                "task-123",
                "ISSUE_COMMENT",
                "older safety reason",
                Instant.parse("2026-06-20T04:00:00Z")
        ));
        FixTaskPreExecutionDecisionVo latest = decisionService.recordDecision(command(
                "task-123",
                "ISSUE_COMMENT",
                "persisted safety reason",
                Instant.parse("2026-06-20T04:01:00Z")
        ));

        assertThat(decisionService.findLatestDecision("task-123")).contains(latest);
        assertThat(decisionService.findLatestDecision("missing-task")).isEmpty();
    }

    @Test
    void should_list_recent_pre_execution_decisions() {
        FixTaskPreExecutionDecisionVo older = decisionService.recordDecision(command(
                "task-older",
                "ISSUE_COMMENT",
                "older safety reason",
                Instant.parse("2026-06-20T04:00:00Z")
        ));
        FixTaskPreExecutionDecisionVo newer = decisionService.recordDecision(command(
                "task-newer",
                "ISSUE_COMMENT",
                "newer safety reason",
                Instant.parse("2026-06-20T04:01:00Z")
        ));

        assertThat(decisionService.listRecentDecisions(1)).containsExactly(newer);
        assertThat(decisionService.listRecentDecisions(10)).containsExactly(newer, older);
        assertThat(decisionService.listRecentDecisions(0)).isEmpty();
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
}
