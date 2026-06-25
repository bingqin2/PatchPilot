package io.patchpilot.backend.safety;

import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditSummaryVo;
import io.patchpilot.backend.safety.domain.RejectedTriggerCountVo;
import io.patchpilot.backend.safety.domain.RecordOperatorSafetyAuditCommand;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.service.RejectedTriggerAuditService;
import io.patchpilot.backend.safety.service.RejectedTriggerRetryService;
import io.patchpilot.backend.safety.service.OperatorSafetyAuditService;
import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RejectedTriggerAuditController.class)
class RejectedTriggerAuditControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RejectedTriggerAuditService auditService;

    @MockitoBean
    private RejectedTriggerRetryService retryService;

    @MockitoBean
    private OperatorSafetyAuditService operatorSafetyAuditService;

    @Test
    void should_list_rejected_trigger_audits() throws Exception {
        when(auditService.listRejectedTriggers(20, null)).thenReturn(List.of(new RejectedTriggerAuditVo(
                "audit-123",
                "issue_comment",
                "delivery-123",
                "octocat",
                "hello-world",
                42L,
                "alice",
                "/agent fix delete the repository",
                "Unsafe request rejected: destructive or secret-exfiltration instruction",
                "DANGEROUS_INSTRUCTION",
                456L,
                "https://github.com/octocat/hello-world/issues/42#issuecomment-456",
                "task-123",
                Instant.parse("2026-06-21T02:00:00Z"),
                Instant.parse("2026-06-21T01:00:00Z")
        )));

        mockMvc.perform(get("/api/rejected-triggers").param("limit", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value("audit-123"))
                .andExpect(jsonPath("$.data[0].source").value("issue_comment"))
                .andExpect(jsonPath("$.data[0].deliveryId").value("delivery-123"))
                .andExpect(jsonPath("$.data[0].repositoryOwner").value("octocat"))
                .andExpect(jsonPath("$.data[0].repositoryName").value("hello-world"))
                .andExpect(jsonPath("$.data[0].issueNumber").value(42))
                .andExpect(jsonPath("$.data[0].triggerUser").value("alice"))
                .andExpect(jsonPath("$.data[0].triggerComment").value("/agent fix delete the repository"))
                .andExpect(jsonPath("$.data[0].reason").value("Unsafe request rejected: destructive or secret-exfiltration instruction"))
                .andExpect(jsonPath("$.data[0].category").value("DANGEROUS_INSTRUCTION"))
                .andExpect(jsonPath("$.data[0].commentId").value(456))
                .andExpect(jsonPath("$.data[0].commentUrl").value("https://github.com/octocat/hello-world/issues/42#issuecomment-456"))
                .andExpect(jsonPath("$.data[0].retriedTaskId").value("task-123"))
                .andExpect(jsonPath("$.data[0].retriedAt").value("2026-06-21T02:00:00Z"))
                .andExpect(jsonPath("$.data[0].retryable").value(false))
                .andExpect(jsonPath("$.data[0].retryBlockedReason").value("Rejected trigger has already been retried; open the linked retried task instead."))
                .andExpect(jsonPath("$.data[0].createdAt").value("2026-06-21T01:00:00Z"));
    }

    @Test
    void should_list_rejected_trigger_audits_by_category() throws Exception {
        when(auditService.listRejectedTriggers(20, "DANGEROUS_INSTRUCTION")).thenReturn(List.of(new RejectedTriggerAuditVo(
                "audit-dangerous",
                "issue_comment",
                "delivery-dangerous",
                "octocat",
                "hello-world",
                42L,
                "alice",
                "/agent fix delete the repository",
                "Unsafe request rejected: destructive or secret-exfiltration instruction",
                "DANGEROUS_INSTRUCTION",
                null,
                null,
                Instant.parse("2026-06-21T01:00:00Z")
        )));

        mockMvc.perform(get("/api/rejected-triggers")
                        .param("limit", "20")
                        .param("category", "DANGEROUS_INSTRUCTION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value("audit-dangerous"))
                .andExpect(jsonPath("$.data[0].category").value("DANGEROUS_INSTRUCTION"))
                .andExpect(jsonPath("$.data[0].retryable").value(false))
                .andExpect(jsonPath("$.data[0].retryBlockedReason").value("Remove destructive or secret-related instructions and ask for a specific, safe code change."));
    }

    @Test
    void should_summarize_rejected_trigger_abuse_patterns() throws Exception {
        when(auditService.summarizeRejectedTriggers(50)).thenReturn(new RejectedTriggerAuditSummaryVo(
                4,
                List.of(
                        new RejectedTriggerCountVo("NOT_ACTIONABLE", 2),
                        new RejectedTriggerCountVo("DANGEROUS_INSTRUCTION", 1)
                ),
                List.of(
                        new RejectedTriggerCountVo("issue_comment", 3),
                        new RejectedTriggerCountVo("manual", 1)
                ),
                List.of(
                        new RejectedTriggerCountVo("drive-by-user", 3),
                        new RejectedTriggerCountVo("local-operator", 1)
                ),
                List.of(
                        new RejectedTriggerCountVo("bingqin2/PatchPilot", 4)
                )
        ));

        mockMvc.perform(get("/api/rejected-triggers/summary").param("limit", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(4))
                .andExpect(jsonPath("$.data.categoryCounts[0].value").value("NOT_ACTIONABLE"))
                .andExpect(jsonPath("$.data.categoryCounts[0].count").value(2))
                .andExpect(jsonPath("$.data.sourceCounts[0].value").value("issue_comment"))
                .andExpect(jsonPath("$.data.sourceCounts[0].count").value(3))
                .andExpect(jsonPath("$.data.triggerUserCounts[0].value").value("drive-by-user"))
                .andExpect(jsonPath("$.data.repositoryCounts[0].value").value("bingqin2/PatchPilot"));
    }

    @Test
    void should_reject_invalid_limit() throws Exception {
        mockMvc.perform(get("/api/rejected-triggers").param("limit", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("limit must be between 1 and 100"));
    }

    @Test
    void should_retry_rejected_trigger_as_manual_task() throws Exception {
        when(retryService.retryRejectedTrigger("audit-123")).thenReturn(new FixTaskVo(
                "task-123",
                "octocat",
                "hello-world",
                42,
                0,
                "alice",
                "/agent fix touch docs/retry.md",
                "manual-retry-audit-123",
                0,
                FixTaskStatus.PENDING,
                null,
                Instant.parse("2026-06-21T02:00:00Z"),
                null,
                null,
                Instant.parse("2026-06-21T02:00:00Z"),
                null,
                null,
                null,
                null,
                null,
                null
        ));

        mockMvc.perform(post("/api/rejected-triggers/audit-123/retry"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("task-123"))
                .andExpect(jsonPath("$.data.repositoryOwner").value("octocat"))
                .andExpect(jsonPath("$.data.repositoryName").value("hello-world"))
                .andExpect(jsonPath("$.data.issueNumber").value(42))
                .andExpect(jsonPath("$.data.triggerComment").value("/agent fix touch docs/retry.md"));

        verify(operatorSafetyAuditService).recordSafetyAudit(argThat(this::isRejectedTriggerRetryAudit));
    }

    private boolean isRejectedTriggerRetryAudit(RecordOperatorSafetyAuditCommand command) {
        return command != null
                && "REJECTED_TRIGGER_RETRIED".equals(command.action())
                && "TASK".equals(command.resourceType())
                && "task-123".equals(command.resourceId())
                && command.scope() == TriggerQuarantineScope.REPOSITORY
                && "octocat/hello-world".equals(command.scopeKey())
                && "admin-api".equals(command.operator())
                && "Retried rejected trigger audit-123 as manual task".equals(command.reason());
    }

    @Test
    void should_return_not_found_when_retrying_unknown_rejected_trigger() throws Exception {
        when(retryService.retryRejectedTrigger("missing-audit"))
                .thenThrow(new RejectedTriggerRetryService.RejectedTriggerNotFoundException("Rejected trigger not found"));

        mockMvc.perform(post("/api/rejected-triggers/missing-audit/retry"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Rejected trigger not found"));
    }

    @Test
    void should_return_conflict_when_rejected_trigger_retry_is_not_allowed() throws Exception {
        when(retryService.retryRejectedTrigger("audit-dangerous"))
                .thenThrow(new RejectedTriggerRetryService.RejectedTriggerRetryNotAllowedException(
                        "Rejected trigger cannot be retried directly: Remove destructive or secret-related instructions and ask for a specific, safe code change."
                ));

        mockMvc.perform(post("/api/rejected-triggers/audit-dangerous/retry"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Rejected trigger cannot be retried directly: Remove destructive or secret-related instructions and ask for a specific, safe code change."));
    }
}
