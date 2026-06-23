package io.patchpilot.backend.safety.service;

import io.patchpilot.backend.safety.domain.RecordRejectedTriggerCommand;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditSummaryVo;
import io.patchpilot.backend.safety.domain.RejectedTriggerCountVo;
import io.patchpilot.backend.safety.domain.TriggerQuarantineScope;
import io.patchpilot.backend.safety.service.impl.InMemoryRejectedTriggerAuditService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryRejectedTriggerAuditServiceTests {

    private final RejectedTriggerAuditService auditService = new InMemoryRejectedTriggerAuditService();

    @Test
    void should_record_rejected_trigger_and_list_newest_first() {
        RejectedTriggerAuditVo older = auditService.recordRejectedTrigger(command(
                "issue_comment",
                "delivery-older",
                "octocat",
                "hello-world",
                42,
                "alice",
                "/agent fix delete the repository",
                "Unsafe request rejected: destructive or secret-exfiltration instruction"
        ));
        RejectedTriggerAuditVo newer = auditService.recordRejectedTrigger(command(
                "manual",
                "manual-newer",
                "bingqin2",
                "PatchPilot",
                7,
                "unknown-user",
                "/agent fix touch docs/demo.md",
                "Unsafe request rejected: trigger user is not allowed"
        ));

        List<RejectedTriggerAuditVo> audits = auditService.listRejectedTriggers(10);

        assertThat(older.id()).isNotBlank();
        assertThat(older.createdAt()).isNotNull();
        assertThat(newer.id()).isNotBlank();
        assertThat(newer.commentId()).isEqualTo(456L);
        assertThat(newer.commentUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/issues/7#issuecomment-456");
        assertThat(audits)
                .extracting(RejectedTriggerAuditVo::deliveryId)
                .containsExactly("manual-newer", "delivery-older");
        assertThat(audits.get(0).reason()).isEqualTo("Unsafe request rejected: trigger user is not allowed");
        assertThat(audits.get(0).category()).isEqualTo("TRIGGER_USER_NOT_ALLOWED");
    }

    @Test
    void should_apply_list_limit() {
        auditService.recordRejectedTrigger(command("manual", "manual-1"));
        auditService.recordRejectedTrigger(command("manual", "manual-2"));
        auditService.recordRejectedTrigger(command("manual", "manual-3"));

        List<RejectedTriggerAuditVo> audits = auditService.listRejectedTriggers(2);

        assertThat(audits)
                .extracting(RejectedTriggerAuditVo::deliveryId)
                .containsExactly("manual-3", "manual-2");
    }

    @Test
    void should_filter_rejected_triggers_by_category() {
        auditService.recordRejectedTrigger(command(
                "issue_comment",
                "dangerous-delivery",
                "octocat",
                "hello-world",
                42,
                "alice",
                "/agent fix delete repo",
                "Unsafe request rejected: destructive or secret-exfiltration instruction"
        ));
        auditService.recordRejectedTrigger(command(
                "issue_comment",
                "not-actionable-delivery",
                "octocat",
                "hello-world",
                42,
                "alice",
                "/agent fix make it better",
                "Unsafe request rejected: instruction is not actionable"
        ));

        List<RejectedTriggerAuditVo> audits = auditService.listRejectedTriggers(10, "NOT_ACTIONABLE");

        assertThat(audits)
                .extracting(RejectedTriggerAuditVo::deliveryId)
                .containsExactly("not-actionable-delivery");
        assertThat(audits.get(0).category()).isEqualTo("NOT_ACTIONABLE");
    }

    @Test
    void should_list_rejected_triggers_for_trigger_user_quarantine() {
        auditService.recordRejectedTrigger(command(
                "issue_comment",
                "delivery-user-match",
                "bingqin2",
                "PatchPilot",
                1,
                "Drive-By-User",
                "/agent fix make it better",
                "Unsafe request rejected: instruction is not actionable"
        ));
        auditService.recordRejectedTrigger(command(
                "issue_comment",
                "delivery-other-user",
                "bingqin2",
                "PatchPilot",
                1,
                "trusted-user",
                "/agent fix make it better",
                "Unsafe request rejected: instruction is not actionable"
        ));

        List<RejectedTriggerAuditVo> audits = auditService.listRejectedTriggersForQuarantine(
                TriggerQuarantineScope.TRIGGER_USER,
                "drive-by-user",
                20
        );

        assertThat(audits)
                .extracting(RejectedTriggerAuditVo::deliveryId)
                .containsExactly("delivery-user-match");
    }

    @Test
    void should_list_rejected_triggers_for_repository_quarantine() {
        auditService.recordRejectedTrigger(command(
                "issue_comment",
                "delivery-repository-match",
                "Bingqin2",
                "PatchPilot",
                1,
                "drive-by-user",
                "/agent fix make it better",
                "Unsafe request rejected: instruction is not actionable"
        ));
        auditService.recordRejectedTrigger(command(
                "issue_comment",
                "delivery-other-repository",
                "octocat",
                "hello-world",
                42,
                "drive-by-user",
                "/agent fix make it better",
                "Unsafe request rejected: instruction is not actionable"
        ));

        List<RejectedTriggerAuditVo> audits = auditService.listRejectedTriggersForQuarantine(
                TriggerQuarantineScope.REPOSITORY,
                "bingqin2/patchpilot",
                20
        );

        assertThat(audits)
                .extracting(RejectedTriggerAuditVo::deliveryId)
                .containsExactly("delivery-repository-match");
    }

    @Test
    void should_summarize_rejected_triggers_by_category_source_user_and_repository() {
        auditService.recordRejectedTrigger(command(
                "issue_comment",
                "delivery-vague-1",
                "bingqin2",
                "PatchPilot",
                1,
                "drive-by-user",
                "/agent fix make it better",
                "Unsafe request rejected: instruction is not actionable"
        ));
        auditService.recordRejectedTrigger(command(
                "issue_comment",
                "delivery-vague-2",
                "bingqin2",
                "PatchPilot",
                1,
                "drive-by-user",
                "/agent fix improve code",
                "Unsafe request rejected: instruction is not actionable"
        ));
        auditService.recordRejectedTrigger(command(
                "issue_comment",
                "delivery-dangerous",
                "bingqin2",
                "PatchPilot",
                2,
                "drive-by-user",
                "/agent fix print secrets",
                "Unsafe request rejected: destructive or secret-exfiltration instruction"
        ));
        auditService.recordRejectedTrigger(command(
                "manual",
                "manual-not-allowed",
                "octocat",
                "hello-world",
                42,
                "local-operator",
                "/agent fix touch docs/demo.md",
                "Unsafe request rejected: trigger user is not allowed"
        ));

        RejectedTriggerAuditSummaryVo summary = auditService.summarizeRejectedTriggers(10);

        assertThat(summary.totalCount()).isEqualTo(4);
        assertThat(summary.categoryCounts()).containsExactly(
                new RejectedTriggerCountVo("NOT_ACTIONABLE", 2),
                new RejectedTriggerCountVo("DANGEROUS_INSTRUCTION", 1),
                new RejectedTriggerCountVo("TRIGGER_USER_NOT_ALLOWED", 1)
        );
        assertThat(summary.sourceCounts()).containsExactly(
                new RejectedTriggerCountVo("issue_comment", 3),
                new RejectedTriggerCountVo("manual", 1)
        );
        assertThat(summary.triggerUserCounts()).containsExactly(
                new RejectedTriggerCountVo("drive-by-user", 3),
                new RejectedTriggerCountVo("local-operator", 1)
        );
        assertThat(summary.repositoryCounts()).containsExactly(
                new RejectedTriggerCountVo("bingqin2/PatchPilot", 3),
                new RejectedTriggerCountVo("octocat/hello-world", 1)
        );
    }

    @Test
    void should_find_rejected_trigger_by_id() {
        RejectedTriggerAuditVo audit = auditService.recordRejectedTrigger(command("manual", "manual-find"));

        assertThat(auditService.findRejectedTrigger(audit.id()))
                .hasValueSatisfying(found -> {
                    assertThat(found.id()).isEqualTo(audit.id());
                    assertThat(found.deliveryId()).isEqualTo("manual-find");
                });
        assertThat(auditService.findRejectedTrigger("missing-audit")).isEmpty();
    }

    @Test
    void should_mark_rejected_trigger_as_retried() {
        RejectedTriggerAuditVo audit = auditService.recordRejectedTrigger(command("manual", "manual-retried"));

        RejectedTriggerAuditVo retried = auditService.markRetried(
                audit.id(),
                "task-123",
                java.time.Instant.parse("2026-06-21T03:00:00Z")
        );

        assertThat(retried.retriedTaskId()).isEqualTo("task-123");
        assertThat(retried.retriedAt()).isEqualTo(java.time.Instant.parse("2026-06-21T03:00:00Z"));
        assertThat(auditService.findRejectedTrigger(audit.id()))
                .hasValueSatisfying(found -> assertThat(found.retriedTaskId()).isEqualTo("task-123"));
    }

    private static RecordRejectedTriggerCommand command(String source, String deliveryId) {
        return command(
                source,
                deliveryId,
                "octocat",
                "hello-world",
                42,
                "alice",
                "/agent fix",
                "Unsafe request rejected"
        );
    }

    private static RecordRejectedTriggerCommand command(
            String source,
            String deliveryId,
            String repositoryOwner,
            String repositoryName,
            long issueNumber,
            String triggerUser,
            String triggerComment,
            String reason
    ) {
        return new RecordRejectedTriggerCommand(
                source,
                deliveryId,
                repositoryOwner,
                repositoryName,
                issueNumber,
                triggerUser,
                triggerComment,
                reason,
                categoryFor(reason),
                "manual".equals(source) ? 456L : null,
                "manual".equals(source) ? "https://github.com/bingqin2/PatchPilot/issues/7#issuecomment-456" : null
        );
    }

    private static String categoryFor(String reason) {
        if ("Unsafe request rejected: trigger user is not allowed".equals(reason)) {
            return "TRIGGER_USER_NOT_ALLOWED";
        }
        if ("Unsafe request rejected: instruction is not actionable".equals(reason)) {
            return "NOT_ACTIONABLE";
        }
        return "DANGEROUS_INSTRUCTION";
    }
}
